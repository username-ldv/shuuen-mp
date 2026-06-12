package ldv.shuuen.ui.screens.training.single.play

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ldv.shuuen.common.ResponseState
import ldv.shuuen.data.audio.training.DegreeContextPlayer
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.domain.audio.engine.MidiEngineStatus
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import ldv.shuuen.domain.training.singles.SinglesLevel
import ldv.shuuen.ui.common.music.Palette
import ldv.shuuen.ui.common.music.inputs.PianoKeyIndication
import kotlin.time.Duration.Companion.milliseconds

enum class AnswerColors(val color: Color) {
  Correct(Color(0xff32cc73)), Incorrect(Color(0xffe74d3c))
}

sealed interface QuizPhase {
  object LoadingContext : QuizPhase
  object AwaitingAnswer : QuizPhase
  object Complete : QuizPhase
}

data class SinglesPlayScreenState(
  val levelData: ResponseState<SinglesLevel> = ResponseState.Loading,
  val phase: QuizPhase = QuizPhase.LoadingContext,
  val quizState: QuizState? = null,
)

val playNoteDuration = 1500.milliseconds

class SinglesPlayScreenViewModel(
  levelId: String, levelRepository: SinglesLocalLevelRepository, val midiEngine: MidiEngine
) : ViewModel() {
  private val _state = MutableStateFlow(SinglesPlayScreenState())
  val state = _state.asStateFlow()

  private lateinit var degreeContextPlayer: DegreeContextPlayer

  private var degreeContextJob: Job? = null
  private var readyStatusJob: Job? = null
  private var setupMelodyNotesIndicationJob: Job? = null

  private var quizzer: SinglesLevelQuizzer? = null
  var lastHandledQuestion = 0
  var lastHandledRoot: Pitch? = null

  // Setup-melody highlight: a single timed indication that follows the currently playing note.
  private val _setupMelodyIndication = MutableStateFlow<PianoKeyIndication?>(null)

  val programmaticIndications: StateFlow<List<PianoKeyIndication>> =
    _setupMelodyIndication
      .map { listOfNotNull(it) }
      .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())


  init {
    Napier.v { "Started level with id: $levelId" }

    viewModelScope.launch {

      when (midiEngine.initialize()) {
        MidiEngineStatus.Ready -> {
          Napier.v { "Initialized MidiEngine" }
        }

        is MidiEngineStatus.Failed -> error("Failed MidiEngine audio initializition")
      }

      lateinit var level: SinglesLevel
      levelRepository.getLevelById(levelId).collect { responseState ->
        _state.update {
          it.copy(levelData = responseState)
        }
        if (responseState !is ResponseState.Success) return@collect
        level = responseState.result
      }
      quizzer = SinglesLevelQuizzer(level)

      val c = level.context

      require(c != null) { "context is null, but need context for now" }

      quizzer?.quizState?.collect { quizState ->
        _state.update { it.copy(quizState = quizState) }

        val isNewQuestion = quizState.currentQuestionNumber != lastHandledQuestion
        val isNewRoot = quizState.root != lastHandledRoot

        if (!isNewQuestion) return@collect

        if (quizState.currentQuestionNumber > (quizState.questionsNumber ?: Int.MAX_VALUE)) {
          _state.update { it.copy(phase = QuizPhase.Complete) }
          return@collect
        }

        lastHandledQuestion = quizState.currentQuestionNumber

        if (isNewRoot) {
          readyStatusJob?.cancel()
          degreeContextJob?.cancel()
          setupMelodyNotesIndicationJob?.cancel()

          _state.update { it.copy(phase = QuizPhase.LoadingContext) }

          degreeContextPlayer = startContext(c, quizState.root)
          lastHandledRoot = quizState.root

          degreeContextPlayer.ready.first { it }
        }

        Napier.v { "Playing ${quizState.currentNote}" }
        playNote(quizState.currentNote)
      }
    }
  }

  /** Returns whether the guess was correct, or null if no quiz is active (caller should not flash). */
  fun userGuessed(pitch: Pitch): Boolean? {
    val quizzer = quizzer ?: return null

    // Correctness must be read before check() advances the question.
    val isCorrect = quizzer.quizState.value.currentNote.pitch == pitch
    quizzer.check(pitch)
    return isCorrect
  }

  fun userGuessed(degree: Degree) {

  }

  fun repeatNote() {
    viewModelScope.launch {
      quizzer?.let { quizzer ->
        quizzer.quizState.value.currentNote.let { playNote(it) }
      }
    }
  }

  private suspend fun playNote(note: Note) {
    midiEngine.playNote(note)
    delay(playNoteDuration)
    midiEngine.stopNote(note)
  }

  private fun startContext(context: DegreeContext, root: Pitch): DegreeContextPlayer {
    Napier.v { "Starting context with pitch $root" }
    val player = DegreeContextPlayer(midiEngine, context, root)

    readyStatusJob = viewModelScope.launch {
      player.ready.collect { ready ->
        Napier.v { "ready state: $ready" }
        _state.update {
          it.copy(phase = if (ready) QuizPhase.AwaitingAnswer else QuizPhase.LoadingContext)
        }
      }
    }
    degreeContextJob = viewModelScope.launch {
      Napier.v { "Starting player..." }
      player.start()
    }
    setupMelodyNotesIndicationJob = viewModelScope.launch {
      _setupMelodyIndication.value = null
      player.setupMelodyNotes.collect { note ->
        Napier.v { "got setup melody note $note" }
        _setupMelodyIndication.value = note?.let {
          val offset = ((it.pitch.ordinal - root.ordinal) % 12 + 12) % 12
          PianoKeyIndication(
            index = it.pitch.ordinal,
            durationMillis = 500,
            color = Palette.entries[offset].color,
          )
        }
      }
    }
    return player
  }
}
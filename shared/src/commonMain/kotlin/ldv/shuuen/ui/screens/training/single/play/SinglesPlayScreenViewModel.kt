package ldv.shuuen.ui.screens.training.single.play

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
import ldv.shuuen.ui.common.music.inputs.PianoKeyIndication
import ldv.shuuen.ui.common.music.inputs.PianoKeyboardDefaults
import kotlin.time.Duration.Companion.milliseconds

enum class AnswerColors(val color: Color) {
  Correct(Color(0xff32cc73)), Incorrect(Color(0xffe74d3c))
}

data class SinglesPlayScreenState(
  val levelData: ResponseState<SinglesLevel> = ResponseState.Loading,
  val isReady: Boolean = false,
  val quizState: QuizState? = null
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

  private val _answerIndications = MutableStateFlow<PianoKeyIndication?>(null)
  val answerIndications = _answerIndications.asStateFlow()
  private val _keyColors = MutableStateFlow(PianoKeyboardDefaults.pressedColors(12))
  val keyColors = _keyColors.asStateFlow()

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

        if (!quizState.newQuestion) return@collect

        if (quizState.needNewContext) {
          readyStatusJob?.cancel()
          degreeContextJob?.cancel()
          setupMelodyNotesIndicationJob?.cancel()

          degreeContextPlayer = startContext(c, quizState.root)
          degreeContextPlayer.ready.first { it }
          quizzer?.initializedContext()
        }

        launch {
          delay(500.milliseconds)
          _keyColors.value = Pitch.entries.map { if (it == quizState.currentNote.pitch) AnswerColors.Correct.color else AnswerColors.Incorrect.color }
        }


        quizzer?.ready()

        Napier.v { "Playing ${quizState.currentNote}" }
        playNote(quizState.currentNote)

      }
    }
  }

  fun userGuessed(pitch: Pitch) {
    quizzer?.check(pitch)
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
          it.copy(isReady = ready)
        }
      }
    }
    degreeContextJob = viewModelScope.launch {
      Napier.v { "Starting player..." }
      player.start()
    }
    setupMelodyNotesIndicationJob = viewModelScope.launch {
      _keyColors.value = PianoKeyboardDefaults.colorfulPressedColors(12, root)
      player.setupMelodyNotes.collect { note ->
        Napier.v { "got setup melody note $note" }
        _answerIndications.value = note?.let {
          PianoKeyIndication(it.pitch.ordinal, 500)
        }
      }
    }
    return player
  }
}
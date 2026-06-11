package ldv.shuuen.ui.screens.training.single.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import ldv.shuuen.domain.training.singles.SinglesLevel
import ldv.shuuen.ui.common.music.inputs.PianoKeyIndication
import kotlin.time.Duration.Companion.milliseconds

data class SinglesPlayScreenState(
  val levelData: ResponseState<SinglesLevel> = ResponseState.Loading,
  val isReady: Boolean = false,
  val root: Pitch? = null
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
  lateinit var quizzer: SinglesLevelQuizzer
  private val _answerIndications = MutableStateFlow<PianoKeyIndication?>(null)
  val answerIndications = _answerIndications.asStateFlow()

  init {
    Napier.v { "Started level with id: $levelId" }

    viewModelScope.launch {
      when (midiEngine.initialize()) {
        MidiEngineStatus.Ready -> {
          Napier.v { "Initialized MidiEngine" }
        }

        is MidiEngineStatus.Failed -> error("Failed MidiEngine audio initializition")
      }
      levelRepository.getLevelById(levelId).collect { responseState ->
        _state.update {
          it.copy(levelData = responseState)
        }
        if (responseState !is ResponseState.Success) return@collect

        val level = responseState.result
        quizzer = SinglesLevelQuizzer(level, midiEngine)

        require(level.context != null) { "context is null, but need context for now" }
        val root: Pitch = quizzer.currentRoot.value
        _state.update { it.copy(root = root)  }
        degreeContextPlayer = startContext(level.context, root)
        degreeContextPlayer.ready.first { it }
        quizzer.currentNote.collect {
          Napier.v { "Playing $it" }
          midiEngine.playNote(it)
          delay(playNoteDuration)
          midiEngine.stopNote(it)
        }
      }
    }
  }

  fun userGuessed(pitch: Pitch) {

  }

  fun userGuessed(degree: Degree) {

  }

  fun repeatNote() {

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
      player.setupMelodyNotes.collect { note ->
        Napier.v { "got setup melody note $note" }
        _answerIndications.value = note?.let {
          PianoKeyIndication(it.pitch.ordinal, 500)
        }
        Napier.v { "_answerIndications ${_answerIndications.value}" }
      }
    }
    return player
  }
}
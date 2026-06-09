package ldv.shuuen.ui.screens.training.single.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ldv.shuuen.common.ResponseState
import ldv.shuuen.data.audio.training.DegreeContextPlayer
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.domain.audio.engine.MidiEngineStatus
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.training.singles.SinglesLevel

data class SinglesPlayScreenState(
  val levelData: ResponseState<SinglesLevel> = ResponseState.Loading,
  val isReady: Boolean = false,
  val pitch: Pitch = Pitch.random()
)

class SinglesPlayScreenViewModel(
  levelId: String, settingsRepository: SettingsRepository, val midiEngine: MidiEngine
) : ViewModel() {
  private val _state = MutableStateFlow(SinglesPlayScreenState())
  val state = _state.asStateFlow()
  var degreeContextPlayer: DegreeContextPlayer? = null
  private var degreeContextJob: Job? = null
  private var readyStatusJob: Job? = null

  init {
    Napier.v { "Started level with id: $levelId" }

    viewModelScope.launch {
      when (midiEngine.initialize()) {
        MidiEngineStatus.Ready -> {
          Napier.v { "Initialized MidiEngine" }
        }

        is MidiEngineStatus.Failed -> error("Failed MidiEngine audio initializition")
      }
      settingsRepository.getLevelById(levelId).collect { responseState ->
        _state.update {
          it.copy(levelData = responseState)
        }
        if (responseState is ResponseState.Success) startContext(
          responseState.result, state.value.pitch
        )
      }
    }
  }

  fun startContext(level: SinglesLevel, pitch: Pitch) {
    Napier.v { "Random pitch: $pitch" }
    val player = DegreeContextPlayer(midiEngine, level.context, pitch)
    degreeContextPlayer = player

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
  }
}
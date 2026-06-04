package ldv.shuuen.data.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.midi.MidiChannel
import ldv.shuuen.domain.audio.midi.Preset
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.repository.AppSettings

class InMemorySettingsRepository(
  initialSettings: AppSettings = AppSettings(),
) : SettingsRepository {
  private val state = MutableStateFlow(initialSettings)

  override val settings: StateFlow<AppSettings> = state

  override suspend fun setSoundFontPath(path: String?) {
    state.update { it.copy(soundFontPath = path) }
  }

  override suspend fun setPreset(channel: MidiChannel, preset: Preset) {
    state.update { current ->
      current.copy(
        presets = when (channel) {
          MidiChannel.Notes -> current.presets.copy(notes = preset)
          MidiChannel.Drone -> current.presets.copy(drone = preset)
          MidiChannel.Cadence -> current.presets.copy(cadence = preset)
        },
      )
    }
  }
}

package ldv.shuuen.data.settings

import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ldv.shuuen.domain.audio.midi.MidiChannel
import ldv.shuuen.domain.audio.midi.Preset
import ldv.shuuen.domain.repository.AppSettings
import ldv.shuuen.domain.repository.SettingsRepository

class KStoreSettingsRepository(
  private val store: KStore<AppSettings>,
) : SettingsRepository {
  override val settings: Flow<AppSettings> = store.updates.map { it ?: AppSettings() }

  override suspend fun setPreset(channel: MidiChannel, preset: Preset) {
    store.update {
      it?.copy(
        presets = when (channel) {
          MidiChannel.Notes -> it.presets.copy(notes = preset)
          MidiChannel.Drone -> it.presets.copy(drone = preset)
          MidiChannel.Cadence -> it.presets.copy(cadence = preset)
        },
      )
    }
  }

  override suspend fun setSoundFontPath(path: String?) {
    store.update { it?.copy(soundFontPath = path) }
  }
}

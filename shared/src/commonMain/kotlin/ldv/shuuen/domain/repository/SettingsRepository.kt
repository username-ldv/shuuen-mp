package ldv.shuuen.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.midi.ChannelPresets
import ldv.shuuen.domain.audio.midi.DefaultPreset
import ldv.shuuen.domain.audio.midi.MidiChannel
import ldv.shuuen.domain.audio.midi.Preset

interface SettingsRepository {
  val settings: Flow<AppSettings>

  suspend fun setSoundFontPath(path: String?)

  suspend fun setPreset(channel: MidiChannel, preset: Preset)
}

@Serializable
data class AppSettings(
  val soundFontPath: String? = null,
  val presets: ChannelPresets = ChannelPresets(
    notes = DefaultPreset.Notes.preset,
    drone = DefaultPreset.Drone.preset,
    cadence = DefaultPreset.Cadence.preset,
  ),
)
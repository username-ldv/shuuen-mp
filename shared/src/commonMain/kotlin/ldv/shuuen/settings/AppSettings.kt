package ldv.shuuen.settings

import kotlinx.coroutines.flow.Flow
import ldv.shuuen.audio.ChannelPresets
import ldv.shuuen.audio.DefaultPreset
import ldv.shuuen.audio.MidiChannel
import ldv.shuuen.audio.Preset

data class AppSettings(
  val soundFontPath: String? = null,
  val presets: ChannelPresets = ChannelPresets(
    notes = DefaultPreset.Notes.preset,
    drone = DefaultPreset.Drone.preset,
    cadence = DefaultPreset.Cadence.preset,
  ),
)

interface SettingsRepository {
  val settings: Flow<AppSettings>

  suspend fun setSoundFontPath(path: String?)

  suspend fun setPreset(channel: MidiChannel, preset: Preset)
}

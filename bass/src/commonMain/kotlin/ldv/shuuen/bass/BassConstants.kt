package ldv.shuuen.bass

internal expect object BassConstants {
  val BASS_OK: Int
  val BASS_ERROR_INIT: Int
  val BASS_CONFIG_BUFFER: Int
  val BASS_CONFIG_UPDATEPERIOD: Int
  val BASS_CONFIG_DEV_BUFFER: Int
  val BASS_CONFIG_DEV_PERIOD: Int
  val BASS_ATTRIB_BUFFER: Int
  val BASS_STREAM_DECODE: Int
  val BASS_MIDI_DECAYEND: Int
  val BASS_CONFIG_MIDI_VOICES: Int
  val MIDI_EVENT_NOTE: Int
  val MIDI_EVENT_PROGRAM: Int
  val MIDI_EVENT_BANK: Int
  val MIDI_EVENT_VOLUME: Int
  val MIDI_EVENT_NOTESOFF: Int
}

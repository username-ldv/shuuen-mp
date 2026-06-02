package ldv.shuuen.bass

internal actual object BassConstants {
  actual val BASS_OK: Int = 0
  actual val BASS_ERROR_INIT: Int = 8
  actual val BASS_CONFIG_BUFFER: Int = 0
  actual val BASS_CONFIG_UPDATEPERIOD: Int = 1
  actual val BASS_CONFIG_DEV_BUFFER: Int = 27
  actual val BASS_CONFIG_DEV_PERIOD: Int = 53
  actual val BASS_ATTRIB_BUFFER: Int = 13
  actual val BASS_STREAM_DECODE: Int = 0x200000
  actual val BASS_MIDI_DECAYEND: Int = 0x1000
  actual val BASS_CONFIG_MIDI_VOICES: Int = 0x10401
  actual val MIDI_EVENT_NOTE: Int = 1
  actual val MIDI_EVENT_PROGRAM: Int = 2
  actual val MIDI_EVENT_BANK: Int = 10
  actual val MIDI_EVENT_VOLUME: Int = 12
  actual val MIDI_EVENT_NOTESOFF: Int = 18
}

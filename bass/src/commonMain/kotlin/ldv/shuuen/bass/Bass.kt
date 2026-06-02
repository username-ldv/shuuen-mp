package ldv.shuuen.bass

object Bass {
  val BASS_OK: Int
    get() = BassConstants.BASS_OK
  val BASS_ERROR_INIT: Int
    get() = BassConstants.BASS_ERROR_INIT
  val BASS_CONFIG_BUFFER: Int
    get() = BassConstants.BASS_CONFIG_BUFFER
  val BASS_CONFIG_UPDATEPERIOD: Int
    get() = BassConstants.BASS_CONFIG_UPDATEPERIOD
  val BASS_CONFIG_DEV_BUFFER: Int
    get() = BassConstants.BASS_CONFIG_DEV_BUFFER
  val BASS_CONFIG_DEV_PERIOD: Int
    get() = BassConstants.BASS_CONFIG_DEV_PERIOD
  val BASS_ATTRIB_BUFFER: Int
    get() = BassConstants.BASS_ATTRIB_BUFFER
  val BASS_STREAM_DECODE: Int
    get() = BassConstants.BASS_STREAM_DECODE
  val BASS_MIDI_DECAYEND: Int
    get() = BassConstants.BASS_MIDI_DECAYEND
  val BASS_CONFIG_MIDI_VOICES: Int
    get() = BassConstants.BASS_CONFIG_MIDI_VOICES
  val MIDI_EVENT_NOTE: Int
    get() = BassConstants.MIDI_EVENT_NOTE
  val MIDI_EVENT_PROGRAM: Int
    get() = BassConstants.MIDI_EVENT_PROGRAM
  val MIDI_EVENT_BANK: Int
    get() = BassConstants.MIDI_EVENT_BANK
  val MIDI_EVENT_VOLUME: Int
    get() = BassConstants.MIDI_EVENT_VOLUME
  val MIDI_EVENT_NOTESOFF: Int
    get() = BassConstants.MIDI_EVENT_NOTESOFF

  fun load() = BassPlatform.load()

  fun version(): Int = BassPlatform.version()

  fun midiVersion(): Int = BassPlatform.midiVersion()

  fun versionText(): String = version().toBassVersionText()

  fun midiVersionText(): String = midiVersion().toBassVersionText()

  fun init(device: Int = -1, frequency: Int = 44_100, flags: Int = 0): Boolean =
    BassPlatform.init(device, frequency, flags)

  fun setConfig(option: Int, value: Int): Boolean = BassPlatform.setConfig(option, value)

  fun free(): Boolean = BassPlatform.free()

  fun freePlugins(handle: Int = 0): Boolean = BassPlatform.freePlugins(handle)

  fun errorCode(): Int = BassPlatform.errorCode()

  fun createLiveMidiStream(
    channels: Int = 128,
    flags: Int = 0,
    frequency: Int = 44_100,
  ): Int = BassPlatform.createLiveMidiStream(channels, flags, frequency)

  fun createMidiStream(
    filePath: String,
    flags: Int = 0,
    frequency: Int = 44_100,
  ): Int = BassPlatform.createMidiStream(filePath, flags, frequency)

  fun loadSoundFont(filePath: String, flags: Int = 0): Int =
    BassPlatform.loadSoundFont(filePath, flags)

  fun setStreamSoundFont(
    streamHandle: Int,
    soundFontHandle: Int,
    preset: Int = -1,
    bank: Int = 0,
  ): Boolean = BassPlatform.setStreamSoundFont(streamHandle, soundFontHandle, preset, bank)

  fun play(channelHandle: Int, restart: Boolean = false): Boolean =
    BassPlatform.play(channelHandle, restart)

  fun start(channelHandle: Int): Boolean =
    BassPlatform.start(channelHandle)

  fun setChannelAttribute(channelHandle: Int, attribute: Int, value: Float): Boolean =
    BassPlatform.setChannelAttribute(channelHandle, attribute, value)

  fun streamEvent(streamHandle: Int, channel: Int, event: Int, parameter: Int): Boolean =
    BassPlatform.streamEvent(streamHandle, channel, event, parameter)

  fun getSoundFontPresets(soundFontHandle: Int): List<Int> =
    BassPlatform.getSoundFontPresets(soundFontHandle)

  fun getSoundFontPresetName(soundFontHandle: Int, preset: Int, bank: Int): String? =
    BassPlatform.getSoundFontPresetName(soundFontHandle, preset, bank)

  fun freeStream(streamHandle: Int): Boolean = BassPlatform.freeStream(streamHandle)

  fun freeSoundFont(soundFontHandle: Int): Boolean =
    BassPlatform.freeSoundFont(soundFontHandle)

  fun makeWord(low: Int, high: Int): Int = (low and 0xff) or ((high and 0xff) shl 8)
}

internal expect object BassPlatform {
  fun load()
  fun version(): Int
  fun midiVersion(): Int
  fun init(device: Int, frequency: Int, flags: Int): Boolean
  fun setConfig(option: Int, value: Int): Boolean
  fun free(): Boolean
  fun freePlugins(handle: Int): Boolean
  fun errorCode(): Int
  fun createLiveMidiStream(channels: Int, flags: Int, frequency: Int): Int
  fun createMidiStream(filePath: String, flags: Int, frequency: Int): Int
  fun loadSoundFont(filePath: String, flags: Int): Int
  fun setStreamSoundFont(streamHandle: Int, soundFontHandle: Int, preset: Int, bank: Int): Boolean
  fun play(channelHandle: Int, restart: Boolean): Boolean
  fun start(channelHandle: Int): Boolean
  fun setChannelAttribute(channelHandle: Int, attribute: Int, value: Float): Boolean
  fun streamEvent(streamHandle: Int, channel: Int, event: Int, parameter: Int): Boolean
  fun getSoundFontPresets(soundFontHandle: Int): List<Int>
  fun getSoundFontPresetName(soundFontHandle: Int, preset: Int, bank: Int): String?
  fun freeStream(streamHandle: Int): Boolean
  fun freeSoundFont(soundFontHandle: Int): Boolean
}

private fun Int.toBassVersionText(): String {
  val major = (this shr 24) and 0xff
  val minor = (this shr 16) and 0xff
  val revision = (this shr 8) and 0xff
  val build = this and 0xff
  return "$major.$minor.$revision.$build"
}

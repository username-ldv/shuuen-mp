package ldv.shuuen.bass

object Bass {
    const val BASS_OK: Int = 0
    const val BASS_ERROR_INIT: Int = 8
    const val BASS_CONFIG_BUFFER: Int = 0
    const val BASS_CONFIG_UPDATEPERIOD: Int = 1
    const val BASS_CONFIG_DEV_BUFFER: Int = 27
    const val BASS_CONFIG_DEV_PERIOD: Int = 53
    const val BASS_ATTRIB_BUFFER: Int = 13
    const val BASS_STREAM_DECODE: Int = 0x200000
    const val BASS_MIDI_DECAYEND: Int = 0x1000
    const val BASS_CONFIG_MIDI_VOICES: Int = 0x10401
    const val MIDI_EVENT_NOTE: Int = 1
    const val MIDI_EVENT_PROGRAM: Int = 2
    const val MIDI_EVENT_BANK: Int = 10
    const val MIDI_EVENT_VOLUME: Int = 12
    const val MIDI_EVENT_NOTESOFF: Int = 18

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

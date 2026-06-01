package ldv.shuuen.bass

object Bass {
    const val BASS_OK: Int = 0
    const val BASS_ERROR_INIT: Int = 8
    const val BASS_STREAM_DECODE: Int = 0x200000
    const val BASS_MIDI_DECAYEND: Int = 0x1000

    fun load() = BassPlatform.load()

    fun version(): Int = BassPlatform.version()

    fun midiVersion(): Int = BassPlatform.midiVersion()

    fun versionText(): String = version().toBassVersionText()

    fun midiVersionText(): String = midiVersion().toBassVersionText()

    fun init(device: Int = -1, frequency: Int = 44_100, flags: Int = 0): Boolean =
        BassPlatform.init(device, frequency, flags)

    fun free(): Boolean = BassPlatform.free()

    fun errorCode(): Int = BassPlatform.errorCode()

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

    fun freeStream(streamHandle: Int): Boolean = BassPlatform.freeStream(streamHandle)

    fun freeSoundFont(soundFontHandle: Int): Boolean =
        BassPlatform.freeSoundFont(soundFontHandle)
}

internal expect object BassPlatform {
    fun load()
    fun version(): Int
    fun midiVersion(): Int
    fun init(device: Int, frequency: Int, flags: Int): Boolean
    fun free(): Boolean
    fun errorCode(): Int
    fun createMidiStream(filePath: String, flags: Int, frequency: Int): Int
    fun loadSoundFont(filePath: String, flags: Int): Int
    fun setStreamSoundFont(streamHandle: Int, soundFontHandle: Int, preset: Int, bank: Int): Boolean
    fun play(channelHandle: Int, restart: Boolean): Boolean
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

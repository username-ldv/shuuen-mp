package ldv.shuuen.bass

import com.un4seen.bass.BASS
import com.un4seen.bass.BASSMIDI

internal actual object BassPlatform {
    actual fun load() {
        BASS.BASS_GetVersion()
        BASSMIDI.BASS_MIDI_GetVersion()
    }

    actual fun version(): Int = BASS.BASS_GetVersion()

    actual fun midiVersion(): Int = BASSMIDI.BASS_MIDI_GetVersion()

    actual fun init(device: Int, frequency: Int, flags: Int): Boolean =
        BASS.BASS_Init(device, frequency, flags)

    actual fun free(): Boolean = BASS.BASS_Free()

    actual fun errorCode(): Int = BASS.BASS_ErrorGetCode()

    actual fun createMidiStream(filePath: String, flags: Int, frequency: Int): Int =
        BASSMIDI.BASS_MIDI_StreamCreateFile(filePath, 0, 0, flags, frequency)

    actual fun loadSoundFont(filePath: String, flags: Int): Int =
        BASSMIDI.BASS_MIDI_FontInit(filePath, flags)

    actual fun setStreamSoundFont(
        streamHandle: Int,
        soundFontHandle: Int,
        preset: Int,
        bank: Int,
    ): Boolean {
        val font = BASSMIDI.BASS_MIDI_FONT().apply {
            font = soundFontHandle
            this.preset = preset
            this.bank = bank
        }
        return BASSMIDI.BASS_MIDI_StreamSetFonts(streamHandle, arrayOf(font), 1)
    }

    actual fun play(channelHandle: Int, restart: Boolean): Boolean =
        BASS.BASS_ChannelPlay(channelHandle, restart)

    actual fun freeStream(streamHandle: Int): Boolean =
        BASS.BASS_StreamFree(streamHandle)

    actual fun freeSoundFont(soundFontHandle: Int): Boolean =
        BASSMIDI.BASS_MIDI_FontFree(soundFontHandle)
}

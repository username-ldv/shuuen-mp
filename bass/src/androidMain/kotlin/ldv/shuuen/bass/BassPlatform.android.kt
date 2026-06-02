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

    actual fun setConfig(option: Int, value: Int): Boolean =
        BASS.BASS_SetConfig(option, value)

    actual fun free(): Boolean = BASS.BASS_Free()

    actual fun freePlugins(handle: Int): Boolean = BASS.BASS_PluginFree(handle)

    actual fun errorCode(): Int = BASS.BASS_ErrorGetCode()

    actual fun createLiveMidiStream(channels: Int, flags: Int, frequency: Int): Int =
        BASSMIDI.BASS_MIDI_StreamCreate(channels, flags, frequency)

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

    actual fun start(channelHandle: Int): Boolean =
        BASS.BASS_ChannelStart(channelHandle)

    actual fun setChannelAttribute(channelHandle: Int, attribute: Int, value: Float): Boolean =
        BASS.BASS_ChannelSetAttribute(channelHandle, attribute, value)

    actual fun streamEvent(streamHandle: Int, channel: Int, event: Int, parameter: Int): Boolean =
        BASSMIDI.BASS_MIDI_StreamEvent(streamHandle, channel, event, parameter)

    actual fun getSoundFontPresets(soundFontHandle: Int): List<Int> {
        val info = BASSMIDI.BASS_MIDI_FONTINFO()
        if (!BASSMIDI.BASS_MIDI_FontGetInfo(soundFontHandle, info)) return emptyList()

        val packedPresets = IntArray(info.presets)
        if (!BASSMIDI.BASS_MIDI_FontGetPresets(soundFontHandle, packedPresets)) return emptyList()
        return packedPresets.toList()
    }

    actual fun getSoundFontPresetName(soundFontHandle: Int, preset: Int, bank: Int): String? =
        BASSMIDI.BASS_MIDI_FontGetPreset(soundFontHandle, preset, bank)

    actual fun freeStream(streamHandle: Int): Boolean =
        BASS.BASS_StreamFree(streamHandle)

    actual fun freeSoundFont(soundFontHandle: Int): Boolean =
        BASSMIDI.BASS_MIDI_FontFree(soundFontHandle)
}

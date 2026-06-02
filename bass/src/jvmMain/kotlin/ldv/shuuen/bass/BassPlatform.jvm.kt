package ldv.shuuen.bass

import com.sun.jna.Function
import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.outputStream

internal actual object BassPlatform {
    private val libraries: NativeLibraries by lazy { NativeLibraries.load() }

    actual fun load() {
        libraries
    }

    actual fun version(): Int = libraries.bass.BASS_GetVersion()

    actual fun midiVersion(): Int = libraries.midi.BASS_MIDI_GetVersion()

    actual fun init(device: Int, frequency: Int, flags: Int): Boolean =
        libraries.bass.BASS_Init(device, frequency, flags, null, null)

    actual fun setConfig(option: Int, value: Int): Boolean =
        libraries.bass.BASS_SetConfig(option, value)

    actual fun free(): Boolean = libraries.bass.BASS_Free()

    actual fun freePlugins(handle: Int): Boolean = libraries.bass.BASS_PluginFree(handle)

    actual fun errorCode(): Int = libraries.bass.BASS_ErrorGetCode()

    actual fun createLiveMidiStream(channels: Int, flags: Int, frequency: Int): Int =
        libraries.midi.BASS_MIDI_StreamCreate(channels, flags, frequency)

    actual fun createMidiStream(filePath: String, flags: Int, frequency: Int): Int =
        libraries.midi.BASS_MIDI_StreamCreateFile(0, filePath, 0, 0, flags, frequency)

    actual fun loadSoundFont(filePath: String, flags: Int): Int =
        libraries.midi.BASS_MIDI_FontInit(filePath, flags)

    actual fun setStreamSoundFont(
        streamHandle: Int,
        soundFontHandle: Int,
        preset: Int,
        bank: Int,
    ): Boolean {
        val font = Memory(12).apply {
            setInt(0, soundFontHandle)
            setInt(4, preset)
            setInt(8, bank)
        }
        return libraries.midi.BASS_MIDI_StreamSetFonts(streamHandle, font, 1)
    }

    actual fun play(channelHandle: Int, restart: Boolean): Boolean =
        libraries.bass.BASS_ChannelPlay(channelHandle, restart)

    actual fun start(channelHandle: Int): Boolean =
        libraries.bass.BASS_ChannelStart(channelHandle)

    actual fun setChannelAttribute(channelHandle: Int, attribute: Int, value: Float): Boolean =
        libraries.bass.BASS_ChannelSetAttribute(channelHandle, attribute, value)

    actual fun streamEvent(streamHandle: Int, channel: Int, event: Int, parameter: Int): Boolean =
        libraries.midi.BASS_MIDI_StreamEvent(streamHandle, channel, event, parameter)

    actual fun getSoundFontPresets(soundFontHandle: Int): List<Int> {
        val info = BassMidiFontInfo()
        if (!libraries.midi.BASS_MIDI_FontGetInfo(soundFontHandle, info)) return emptyList()
        info.read()

        val packedPresets = IntArray(info.presets)
        if (!libraries.midi.BASS_MIDI_FontGetPresets(soundFontHandle, packedPresets)) return emptyList()
        return packedPresets.toList()
    }

    actual fun getSoundFontPresetName(soundFontHandle: Int, preset: Int, bank: Int): String? =
        libraries.midi.BASS_MIDI_FontGetPreset(soundFontHandle, preset, bank)

    actual fun freeStream(streamHandle: Int): Boolean =
        libraries.bass.BASS_StreamFree(streamHandle)

    actual fun freeSoundFont(soundFontHandle: Int): Boolean =
        libraries.midi.BASS_MIDI_FontFree(soundFontHandle)
}

private data class NativeLibraries(
    val bass: BassNative,
    val midi: BassMidiNative,
) {
    companion object {
        fun load(): NativeLibraries {
            val os = currentOs()
            val arch = currentArch()
            val extension = if (os == "windows") "dll" else "so"
            val prefix = if (os == "windows") "" else "lib"
            val bassPath = extractLibrary("/native/$os/$arch/${prefix}bass.$extension")
            val midiPath = extractLibrary("/native/$os/$arch/${prefix}bassmidi.$extension")
            val options = if (os == "windows") {
                mapOf(Library.OPTION_CALLING_CONVENTION to Function.ALT_CONVENTION)
            } else {
                emptyMap<String, Any>()
            }

            // Load BASS before BASSMIDI because the add-on depends on the core library.
            val bass = Native.load(bassPath.toString(), BassNative::class.java, options)
            val midi = Native.load(midiPath.toString(), BassMidiNative::class.java, options)
            return NativeLibraries(bass, midi)
        }
    }
}

private interface BassNative : Library {
    fun BASS_GetVersion(): Int
    fun BASS_ErrorGetCode(): Int
    fun BASS_SetConfig(option: Int, value: Int): Boolean
    fun BASS_Init(device: Int, frequency: Int, flags: Int, win: Pointer?, dsguid: Pointer?): Boolean
    fun BASS_Free(): Boolean
    fun BASS_PluginFree(handle: Int): Boolean
    fun BASS_ChannelPlay(handle: Int, restart: Boolean): Boolean
    fun BASS_ChannelStart(handle: Int): Boolean
    fun BASS_ChannelSetAttribute(handle: Int, attrib: Int, value: Float): Boolean
    fun BASS_StreamFree(handle: Int): Boolean
}

private interface BassMidiNative : Library {
    fun BASS_MIDI_GetVersion(): Int
    fun BASS_MIDI_StreamCreate(channels: Int, flags: Int, frequency: Int): Int
    fun BASS_MIDI_StreamCreateFile(
        filetype: Int,
        file: String,
        offset: Long,
        length: Long,
        flags: Int,
        frequency: Int,
    ): Int

    fun BASS_MIDI_FontInit(file: String, flags: Int): Int
    fun BASS_MIDI_FontFree(handle: Int): Boolean
    fun BASS_MIDI_StreamSetFonts(handle: Int, fonts: Pointer, count: Int): Boolean
    fun BASS_MIDI_StreamEvent(handle: Int, chan: Int, event: Int, param: Int): Boolean
    fun BASS_MIDI_FontGetInfo(handle: Int, info: BassMidiFontInfo): Boolean
    fun BASS_MIDI_FontGetPresets(handle: Int, presets: IntArray): Boolean
    fun BASS_MIDI_FontGetPreset(handle: Int, preset: Int, bank: Int): String?
}

class BassMidiFontInfo : Structure() {
    @JvmField
    var name: Pointer? = null

    @JvmField
    var copyright: Pointer? = null

    @JvmField
    var comment: Pointer? = null

    @JvmField
    var presets: Int = 0

    @JvmField
    var samsize: Int = 0

    @JvmField
    var samload: Int = 0

    @JvmField
    var samtype: Int = 0

    override fun getFieldOrder(): List<String> =
        listOf("name", "copyright", "comment", "presets", "samsize", "samload", "samtype")
}

private fun extractLibrary(resourcePath: String): Path {
    val target = libraryCachePath(resourcePath)
    if (target.exists()) return target

    val stream = BassPlatform::class.java.getResourceAsStream(resourcePath)
        ?: error("BASS native library resource not found: $resourcePath")

    Files.createDirectories(target.parent)
    stream.use { input ->
        target.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return target
}

private fun libraryCachePath(resourcePath: String): Path {
    val fileName = resourcePath.substringAfterLast('/')
    val directory = resourcePath.trim('/').substringBeforeLast('/').replace('/', '-')
    return Path.of(System.getProperty("java.io.tmpdir"), "shuuen-bass", directory, fileName)
}

private fun currentOs(): String {
    val os = System.getProperty("os.name").lowercase()
    return when {
        os.contains("win") -> "windows"
        os.contains("linux") -> "linux"
        else -> error("BASS desktop libraries are bundled only for Windows and Linux; current OS is $os")
    }
}

private fun currentArch(): String {
    val arch = System.getProperty("os.arch").lowercase()
    return when (arch) {
        "amd64", "x86_64" -> "x86_64"
        "x86", "i386", "i686" -> "x86"
        "aarch64", "arm64" -> "aarch64"
        "arm", "arm32" -> "armhf"
        else -> error("Unsupported BASS native architecture: $arch")
    }
}

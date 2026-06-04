package ldv.shuuen.bass

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
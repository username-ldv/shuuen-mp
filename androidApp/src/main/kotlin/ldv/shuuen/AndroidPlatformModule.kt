package ldv.shuuen

import android.content.Context
import android.net.Uri
import com.un4seen.bass.BASS
import com.un4seen.bass.BASSMIDI
import ldv.shuuen.audio.SoundFontProvider
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidPlatformModules(context: Context): List<Module> =
  listOf(
    module {
      single<SoundFontProvider> { AndroidSoundFontProvider(context.applicationContext) }
    },
  )

private class AndroidSoundFontProvider(
  private val context: Context,
) : SoundFontProvider {
  override suspend fun loadSoundFont(location: String): Int {
    val descriptor = runCatching {
      context.contentResolver.openFileDescriptor(Uri.parse(location), "r")
    }.getOrNull()
    return if (descriptor == null) {
      BASSMIDI.BASS_MIDI_FontInit(location, 0)
    } else {
      BASSMIDI.BASS_MIDI_FontInit(descriptor, 0)
    }
  }

  override suspend fun loadDefaultSoundFont(): Int {
    return BASSMIDI.BASS_MIDI_FontInit(BASS.Asset(context.assets, DEFAULT_SOUNDFONT_ASSET), 0)
  }

  private companion object {
    const val DEFAULT_SOUNDFONT_ASSET = "GeneralUser-GS.sf2"
  }
}

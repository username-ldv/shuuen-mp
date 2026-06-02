package ldv.shuuen

import ldv.shuuen.audio.SoundFontProvider
import ldv.shuuen.bass.Bass
import org.koin.core.module.Module
import org.koin.dsl.module
import java.nio.file.Path
import kotlin.io.path.exists

fun desktopPlatformModules(): List<Module> =
  listOf(
    module {
      single<SoundFontProvider> { DesktopSoundFontProvider() }
    },
  )

private class DesktopSoundFontProvider : SoundFontProvider {
  override suspend fun loadSoundFont(location: String): Int =
    Bass.loadSoundFont(location)

  override suspend fun loadDefaultSoundFont(): Int {
    val path = externalDefaultSoundFontPath()
      ?: error("Default soundfont file was not found.")
    return Bass.loadSoundFont(path.toString())
  }

  private fun externalDefaultSoundFontPath(): Path? {
    val resourceDir = System.getProperty("compose.application.resources.dir")
    return listOfNotNull(
      resourceDir?.let { Path.of(it, "soundfonts", DEFAULT_SOUNDFONT_FILE) },
      Path.of("desktopApp", "src", "main", "appResources", "common", "soundfonts", DEFAULT_SOUNDFONT_FILE),
      Path.of("src", "main", "appResources", "common", "soundfonts", DEFAULT_SOUNDFONT_FILE),
    ).firstOrNull { it.exists() }
  }

  private companion object {
    const val DEFAULT_SOUNDFONT_FILE = "GeneralUser-GS.sf2"
  }
}

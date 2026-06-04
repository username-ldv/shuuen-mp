package ldv.shuuen

import ldv.shuuen.domain.audio.engine.SoundFontProvider
import org.koin.core.module.Module
import org.koin.dsl.module

fun desktopPlatformModules(): List<Module> = listOf(
  module {
    single<SoundFontProvider> { DesktopSoundFontProvider() }
  },
)


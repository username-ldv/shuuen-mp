package ldv.shuuen.di

import ldv.shuuen.DesktopSoundFontProvider
import ldv.shuuen.domain.audio.engine.SoundFontProvider
import org.koin.core.logger.Logger
import org.koin.core.logger.PrintLogger
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
  single<PrintLogger>() bind Logger::class

  single<DesktopSoundFontProvider>() bind SoundFontProvider::class
}
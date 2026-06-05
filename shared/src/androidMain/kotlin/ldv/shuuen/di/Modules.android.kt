package ldv.shuuen.di

import ldv.shuuen.AndroidSoundFontProvider
import ldv.shuuen.domain.audio.engine.SoundFontProvider
import org.koin.android.logger.AndroidLogger
import org.koin.core.logger.Logger
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
  single<AndroidLogger>() bind Logger::class

  single<AndroidSoundFontProvider>() bind SoundFontProvider::class
}
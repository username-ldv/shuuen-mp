package ldv.shuuen

import android.content.Context
import ldv.shuuen.domain.audio.engine.SoundFontProvider
import org.koin.core.module.Module
import org.koin.dsl.module

fun androidPlatformModules(context: Context): List<Module> = listOf(
  module {
    single<SoundFontProvider> { AndroidSoundFontProvider(context.applicationContext) }
  },
)
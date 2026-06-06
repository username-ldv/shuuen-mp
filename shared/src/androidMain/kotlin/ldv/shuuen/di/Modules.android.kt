package ldv.shuuen.di

import android.content.Context
import kotlinx.io.files.Path
import ldv.shuuen.AndroidSoundFontProvider
import ldv.shuuen.domain.audio.engine.SoundFontProvider
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
  single<AndroidSoundFontProvider>() bind SoundFontProvider::class

  single<Path>(named("files")) {
    Path(get<Context>().filesDir.toString())
  }
}
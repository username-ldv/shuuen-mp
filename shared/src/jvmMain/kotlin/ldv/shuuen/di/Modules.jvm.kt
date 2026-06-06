package ldv.shuuen.di

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import ldv.shuuen.DesktopSoundFontProvider
import ldv.shuuen.domain.audio.engine.SoundFontProvider
import net.harawata.appdirs.AppDirsFactory
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

actual val platformModule: Module = module {
  single<DesktopSoundFontProvider>() bind SoundFontProvider::class

  single<Path>(named("files")) {
    val path =  Path(AppDirsFactory.getInstance().getUserDataDir("Shuuen", null, null))
    with(SystemFileSystem) { if(!exists(path)) createDirectories(path) }
    path
  }
}
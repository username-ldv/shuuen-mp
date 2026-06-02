package ldv.shuuen.di

import ldv.shuuen.audio.BassMidiEngine
import ldv.shuuen.audio.MidiEngine
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.settings.InMemorySettingsRepository
import ldv.shuuen.settings.SettingsRepository
import ldv.shuuen.singles.SinglesViewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

val commonModule: Module = module {
    single { AppNavigator() }
    single<SettingsRepository> { InMemorySettingsRepository() }
    single<MidiEngine> { BassMidiEngine(get(), get()) }
    single { SinglesViewModel(get()) }
}

fun initShuuenKoin(platformModules: List<Module>) {
    if (GlobalContext.getOrNull() != null) return

    startKoin {
        modules(listOf(commonModule) + platformModules)
    }
}

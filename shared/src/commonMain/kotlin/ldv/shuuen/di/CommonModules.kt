package ldv.shuuen.di

import ldv.shuuen.audio.BassMidiEngine
import ldv.shuuen.audio.MidiEngine
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.navigation.AppRoute
import ldv.shuuen.screens.MainMenuScreen
import ldv.shuuen.screens.SettingsScreen
import ldv.shuuen.settings.InMemorySettingsRepository
import ldv.shuuen.settings.SettingsRepository
import ldv.shuuen.singles.SinglesScreen
import ldv.shuuen.singles.SinglesViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val commonModule: Module = module {
  single { AppNavigator() }
  single<SettingsRepository> { InMemorySettingsRepository() }
  single<MidiEngine> { BassMidiEngine(get(), get()) }
  single { SinglesViewModel(get()) }

  navigation<AppRoute.MainMenu> {
    val navigator = get<AppNavigator>()
    MainMenuScreen(
      onOpenSingles = { navigator.navigateTo(AppRoute.Singles) },
      onOpenSettings = { navigator.navigateTo(AppRoute.Settings) },
    )
  }

  navigation<AppRoute.Singles> {
    val navigator = get<AppNavigator>()
    SinglesScreen(
      viewModel = get(),
      onNavigateBack = { navigator.navigateBack() },
    )
  }

  navigation<AppRoute.Settings> {
    val navigator = get<AppNavigator>()
    SettingsScreen(onNavigateBack = { navigator.navigateBack() })
  }
}

fun initShuuenKoin(platformModules: List<Module>) {
  if (GlobalContext.getOrNull() != null) return

  startKoin {
    modules(listOf(commonModule) + platformModules)
  }
}

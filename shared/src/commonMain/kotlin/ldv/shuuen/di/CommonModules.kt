package ldv.shuuen.di

import ldv.shuuen.audio.BassMidiEngine
import ldv.shuuen.audio.MidiEngine
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.navigation.AppRoute
import ldv.shuuen.screens.MainMenuScreen
import ldv.shuuen.screens.SettingsScreen
import ldv.shuuen.screens.SinglesPlayScreen
import ldv.shuuen.screens.SinglesSetupScreen
import ldv.shuuen.settings.InMemorySettingsRepository
import ldv.shuuen.settings.SettingsRepository
import ldv.shuuen.free_play.FreePlayScreen
import ldv.shuuen.free_play.FreePlayViewModel
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
  single { FreePlayViewModel(get()) }

  navigation<AppRoute.MainMenu> {
    val navigator = get<AppNavigator>()
    MainMenuScreen(
      onOpenFreePlay = { navigator.navigateTo(AppRoute.FreePlay) },
      onOpenSingles = { navigator.navigateTo(AppRoute.SinglesSetup) },
      onOpenSettings = { navigator.navigateTo(AppRoute.Settings) },
    )
  }

  navigation<AppRoute.FreePlay> {
    val navigator = get<AppNavigator>()
    FreePlayScreen(
      viewModel = get(),
      onNavigateBack = { navigator.navigateBack() },
    )
  }

  navigation<AppRoute.Settings> {
    val navigator = get<AppNavigator>()
    SettingsScreen(onNavigateBack = { navigator.navigateBack() })
  }

  navigation<AppRoute.SinglesSetup> {
    val navigator = get<AppNavigator>()
    SinglesSetupScreen(
      onNavigateBack = { navigator.navigateBack() },
      onStartTraining = { navigator.navigateTo(AppRoute.SinglesPlay) },
    )
  }

  navigation<AppRoute.SinglesPlay> {
    val navigator = get<AppNavigator>()
    SinglesPlayScreen(onNavigateBack = { navigator.navigateBack() })
  }
}

fun initShuuenKoin(platformModules: List<Module>) {
  if (GlobalContext.getOrNull() != null) return

  startKoin {
    modules(listOf(commonModule) + platformModules)
  }
}

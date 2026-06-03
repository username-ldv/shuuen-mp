package ldv.shuuen.di

import ldv.shuuen.audio.BassMidiEngine
import ldv.shuuen.audio.MidiEngine
import ldv.shuuen.free_play.FreePlayScreen
import ldv.shuuen.free_play.FreePlayViewModel
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.navigation.AppRoute
import ldv.shuuen.screens.ContextScreen
import ldv.shuuen.screens.LevelCompleteScreen
import ldv.shuuen.screens.LevelSelectScreen
import ldv.shuuen.screens.MainMenuScreen
import ldv.shuuen.screens.MelodiesPlayScreen
import ldv.shuuen.screens.MelodiesSetupScreen
import ldv.shuuen.screens.SettingsScreen
import ldv.shuuen.screens.SinglesPlayScreen
import ldv.shuuen.screens.SinglesSetupScreen
import ldv.shuuen.screens.TrainingFlow
import ldv.shuuen.settings.InMemorySettingsRepository
import ldv.shuuen.settings.SettingsRepository
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
      onOpenMelodies = { navigator.navigateTo(AppRoute.MelodiesLevelSelect) },
      onOpenSingles = { navigator.navigateTo(AppRoute.SinglesLevelSelect) },
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

  navigation<AppRoute.Context> {
    val navigator = get<AppNavigator>()
    ContextScreen(onNavigateBack = { navigator.navigateBack() })
  }

  navigation<AppRoute.SinglesLevelSelect> {
    val navigator = get<AppNavigator>()
    LevelSelectScreen(
      flow = TrainingFlow.Singles,
      onNavigateBack = { navigator.navigateBack() },
      onStartLevel = { navigator.navigateTo(AppRoute.SinglesSetup) },
    )
  }

  navigation<AppRoute.MelodiesLevelSelect> {
    val navigator = get<AppNavigator>()
    LevelSelectScreen(
      flow = TrainingFlow.Melodies,
      onNavigateBack = { navigator.navigateBack() },
      onStartLevel = { navigator.navigateTo(AppRoute.MelodiesSetup) },
    )
  }

  navigation<AppRoute.SinglesLevelComplete> {
    val navigator = get<AppNavigator>()
    LevelCompleteScreen(
      flow = TrainingFlow.Singles,
      onNavigateBack = { navigator.navigateBack() },
      onRetryLevel = { navigator.navigateTo(AppRoute.SinglesSetup) },
      onNextLevel = { navigator.navigateTo(AppRoute.SinglesLevelSelect) },
    )
  }

  navigation<AppRoute.MelodiesLevelComplete> {
    val navigator = get<AppNavigator>()
    LevelCompleteScreen(
      flow = TrainingFlow.Melodies,
      onNavigateBack = { navigator.navigateBack() },
      onRetryLevel = { navigator.navigateTo(AppRoute.MelodiesSetup) },
      onNextLevel = { navigator.navigateTo(AppRoute.MelodiesLevelSelect) },
    )
  }

  navigation<AppRoute.SinglesSetup> {
    val navigator = get<AppNavigator>()
    SinglesSetupScreen(
      onNavigateBack = { navigator.navigateBack() },
      onOpenContext = { navigator.navigateTo(AppRoute.Context) },
      onStartTraining = { navigator.navigateTo(AppRoute.SinglesPlay) },
    )
  }

  navigation<AppRoute.SinglesPlay> {
    val navigator = get<AppNavigator>()
    SinglesPlayScreen(onNavigateBack = { navigator.navigateBack() }, onLevelEnd = {
      navigator.navigateTo(
        AppRoute.SinglesLevelComplete
      )
    })
  }

  navigation<AppRoute.MelodiesSetup> {
    val navigator = get<AppNavigator>()
    MelodiesSetupScreen(
      onNavigateBack = { navigator.navigateBack() },
      onOpenContext = { navigator.navigateTo(AppRoute.Context) },
      onStartTraining = { navigator.navigateTo(AppRoute.MelodiesPlay) },
    )
  }

  navigation<AppRoute.MelodiesPlay> {
    val navigator = get<AppNavigator>()
    MelodiesPlayScreen(onNavigateBack = { navigator.navigateBack() }, onLevelEnd = {
      navigator.navigateTo(AppRoute.MelodiesLevelComplete)
    })
  }
}

fun initShuuenKoin(platformModules: List<Module>) {
  if (GlobalContext.getOrNull() != null) return

  startKoin {
    modules(listOf(commonModule) + platformModules)
  }
}

package ldv.shuuen.di

import ldv.shuuen.data.audio.BassMidiEngine
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.ui.screens.free_play.FreePlayScreen
import ldv.shuuen.ui.screens.free_play.FreePlayViewModel
import ldv.shuuen.ui.navigation.AppNavigator
import ldv.shuuen.ui.navigation.AppRoute
import ldv.shuuen.ui.screens.level_end.LevelCompleteScreen
import ldv.shuuen.ui.screens.level_select.LevelSelectScreen
import ldv.shuuen.ui.screens.main.MainMenuScreen
import ldv.shuuen.ui.screens.training.melodies.play.MelodiesPlayScreen
import ldv.shuuen.ui.screens.training.melodies.setup.MelodiesSetupScreen
import ldv.shuuen.ui.screens.app_settings.SettingsScreen
import ldv.shuuen.ui.screens.training.single.play.SinglesPlayScreen
import ldv.shuuen.ui.screens.training.single.setup.SinglesSetupScreen
import ldv.shuuen.ui.screens.training.common.TrainingFlow
import ldv.shuuen.data.settings.InMemorySettingsRepository
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.ui.screens.context.ContextScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

@OptIn(KoinExperimentalAPI::class)
val commonModule: Module = module {
  single<AppNavigator>()

  single<InMemorySettingsRepository>() bind SettingsRepository::class
  single<BassMidiEngine>() bind MidiEngine::class

  viewModel<FreePlayViewModel>()

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
      viewModel = koinViewModel(),
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
expect val platformModule: Module
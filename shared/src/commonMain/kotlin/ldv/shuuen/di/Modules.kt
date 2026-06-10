package ldv.shuuen.di

import ldv.shuuen.data.audio.BassMidiEngine
import ldv.shuuen.data.database.AppDatabase
import ldv.shuuen.data.database.dao.ContextDao
import ldv.shuuen.data.database.dao.SinglesLevelDao
import ldv.shuuen.data.repository.local.ContextLocalRepositoryImpl
import ldv.shuuen.data.repository.local.SinglesLocalLevelRepositoryImpl
import ldv.shuuen.data.settings.KStoreSettingsRepository
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.repository.local.ContextLocalRepository
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import ldv.shuuen.ui.navigation.AppNavigator
import ldv.shuuen.ui.navigation.AppRoute
import ldv.shuuen.ui.screens.app_settings.SettingsScreen
import ldv.shuuen.ui.screens.context.ContextScreen
import ldv.shuuen.ui.screens.free_play.FreePlayScreen
import ldv.shuuen.ui.screens.free_play.FreePlayViewModel
import ldv.shuuen.ui.screens.level_end.LevelCompleteScreen
import ldv.shuuen.ui.screens.main.MainMenuScreen
import ldv.shuuen.ui.screens.training.common.TrainingFlow
import ldv.shuuen.ui.screens.training.melodies.play.MelodiesPlayScreen
import ldv.shuuen.ui.screens.training.melodies.setup.MelodiesSetupScreen
import ldv.shuuen.ui.screens.training.single.level_select.SinglesLevelSelectScreen
import ldv.shuuen.ui.screens.training.single.level_select.SinglesLevelSelectScreenViewModel
import ldv.shuuen.ui.screens.training.single.play.SinglesPlayScreen
import ldv.shuuen.ui.screens.training.single.play.SinglesPlayScreenViewModel
import ldv.shuuen.ui.screens.training.single.setup.SinglesSetupScreen
import ldv.shuuen.ui.screens.training.single.setup.SinglesSetupScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation
import org.koin.plugin.module.dsl.single
import org.koin.plugin.module.dsl.viewModel

expect val platformModule: Module

@OptIn(KoinExperimentalAPI::class)
val commonModule = module {
  single<KStoreSettingsRepository>() bind SettingsRepository::class

  single<SinglesLevelDao> { get<AppDatabase>().singlesLevelDao() }
  single<ContextDao> { get<AppDatabase>().contextDao() }

  single<ContextLocalRepositoryImpl>() bind ContextLocalRepository::class
  single<SinglesLocalLevelRepositoryImpl>() bind SinglesLocalLevelRepository::class

  single<BassMidiEngine>() bind MidiEngine::class

  single<AppNavigator>()

  navigation<AppRoute.MainMenu> {
    val navigator = get<AppNavigator>()
    MainMenuScreen(
      onOpenFreePlay = { navigator.navigateTo(AppRoute.FreePlay) },
      onOpenMelodies = { navigator.navigateTo(AppRoute.MelodiesLevelSelect) },
      onOpenSingles = { navigator.navigateTo(AppRoute.SinglesLevelSelect) },
      onOpenSettings = { navigator.navigateTo(AppRoute.Settings) },
    )
  }

  viewModel<FreePlayViewModel>()
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


  navigation<AppRoute.MelodiesLevelSelect> {
    val navigator = get<AppNavigator>()
//    LevelSelectScreen(
//      flow = TrainingFlow.Melodies,
//      onNavigateBack = { navigator.navigateBack() },
//      onStartLevel = { navigator.navigateTo(AppRoute.MelodiesSetup) },
//    )
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

  viewModel<SinglesLevelSelectScreenViewModel>()
  navigation<AppRoute.SinglesLevelSelect> {
    val navigator = get<AppNavigator>()
    SinglesLevelSelectScreen(
      onNavigateBack = { navigator.navigateBack() },
      onStartLevel = { levelId -> navigator.navigateTo(AppRoute.SinglesPlay(levelId)) },
      onCreateNewLevel = { navigator.navigateTo(AppRoute.SinglesSetup) },
      viewModel = koinViewModel()
    )
  }

  viewModel<SinglesSetupScreenViewModel>()
  navigation<AppRoute.SinglesSetup> {
    val navigator = get<AppNavigator>()
    SinglesSetupScreen(
      onNavigateBack = { navigator.navigateBack() },
      onOpenContext = { navigator.navigateTo(AppRoute.Context) },
      onSaveLevel = { navigator.navigateBack() },
      viewModel = koinViewModel()
    )
  }

  viewModel<SinglesPlayScreenViewModel>()
  navigation<AppRoute.SinglesPlay> { route ->
    val navigator = get<AppNavigator>()
    SinglesPlayScreen(onNavigateBack = { navigator.navigateBack() }, onLevelEnd = {
      navigator.navigateTo(
        AppRoute.SinglesLevelComplete
      )
    }, viewModel = koinViewModel { parametersOf(route.levelId) })
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

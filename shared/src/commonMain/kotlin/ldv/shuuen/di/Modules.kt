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
import ldv.shuuen.ui.navigation.AppRoute
import ldv.shuuen.ui.navigation.LocalAppNavigator
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

  navigation<AppRoute.MainMenu> {
    val navigator = LocalAppNavigator.current
    MainMenuScreen(
      onOpenFreePlay = { navigator.add(AppRoute.FreePlay) },
      onOpenMelodies = { navigator.add(AppRoute.MelodiesLevelSelect) },
      onOpenSingles = { navigator.add(AppRoute.SinglesLevelSelect) },
      onOpenSettings = { navigator.add(AppRoute.Settings) },
    )
  }

  viewModel<FreePlayViewModel>()
  navigation<AppRoute.FreePlay> {
    val navigator = LocalAppNavigator.current
    FreePlayScreen(
      viewModel = koinViewModel(),
      onNavigateBack = { navigator.goBack() },
    )
  }

  navigation<AppRoute.Settings> {
    val navigator = LocalAppNavigator.current
    SettingsScreen(onNavigateBack = { navigator.goBack() })
  }

  navigation<AppRoute.Context> {
    val navigator = LocalAppNavigator.current
    ContextScreen(onNavigateBack = { navigator.goBack() })
  }


  navigation<AppRoute.MelodiesLevelSelect> {
    val navigator = LocalAppNavigator.current
//    LevelSelectScreen(
//      flow = TrainingFlow.Melodies,
//      onNavigateBack = { navigator.navigateBack() },
//      onStartLevel = { navigator.navigateTo(AppRoute.MelodiesSetup) },
//    )
  }


  navigation<AppRoute.MelodiesLevelComplete> {
    val navigator = LocalAppNavigator.current
    LevelCompleteScreen(
      flow = TrainingFlow.Melodies,
      onNavigateBack = { navigator.goBack() },
      onRetryLevel = { navigator.add(AppRoute.MelodiesSetup) },
      onNextLevel = { navigator.add(AppRoute.MelodiesLevelSelect) },
    )
  }

  viewModel<SinglesLevelSelectScreenViewModel>()
  navigation<AppRoute.SinglesLevelSelect> {
    val navigator = LocalAppNavigator.current
    SinglesLevelSelectScreen(
      onNavigateBack = { navigator.goBack() },
      onStartLevel = { levelId -> navigator.add(AppRoute.SinglesPlay(levelId)) },
      onCreateNewLevel = { navigator.add(AppRoute.SinglesSetup) },
      viewModel = koinViewModel()
    )
  }

  viewModel<SinglesSetupScreenViewModel>()
  navigation<AppRoute.SinglesSetup> {
    val navigator = LocalAppNavigator.current
    SinglesSetupScreen(
      onNavigateBack = { navigator.goBack() },
      onOpenContext = { navigator.add(AppRoute.Context) },
      onSaveLevel = { navigator.goBack() },
      viewModel = koinViewModel()
    )
  }

  viewModel<SinglesPlayScreenViewModel>()
  navigation<AppRoute.SinglesPlay> { route ->
    val navigator = LocalAppNavigator.current
    SinglesPlayScreen(onNavigateBack = { navigator.goBack() }, onLevelEnd = {
      navigator.replaceLastWith(
        AppRoute.SinglesLevelComplete
      )
    }, viewModel = koinViewModel { parametersOf(route.levelId) })
  }

  navigation<AppRoute.SinglesLevelComplete> {
    val navigator = LocalAppNavigator.current
    LevelCompleteScreen(
      flow = TrainingFlow.Singles,
      onNavigateBack = { navigator.goBack() },
      onRetryLevel = { navigator.add(AppRoute.SinglesSetup) },
      onNextLevel = { navigator.add(AppRoute.SinglesLevelSelect) },
    )
  }

  navigation<AppRoute.MelodiesSetup> {
    val navigator = LocalAppNavigator.current
    MelodiesSetupScreen(
      onNavigateBack = { navigator.goBack() },
      onOpenContext = { navigator.add(AppRoute.Context) },
      onStartTraining = { navigator.add(AppRoute.MelodiesPlay) },
    )
  }

  navigation<AppRoute.MelodiesPlay> {
    val navigator = LocalAppNavigator.current
    MelodiesPlayScreen(onNavigateBack = { navigator.goBack() }, onLevelEnd = {
      navigator.add(AppRoute.MelodiesLevelComplete)
    })
  }
}

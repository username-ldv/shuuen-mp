package ldv.shuuen.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute {
  @Serializable
  data object MainMenu : AppRoute

  @Serializable
  data object FreePlay : AppRoute

  @Serializable
  data object Settings : AppRoute

  @Serializable
  data object Context : AppRoute

  @Serializable
  data object SinglesLevelSelect : AppRoute

  @Serializable
  data object MelodiesLevelSelect : AppRoute

  @Serializable
  data object SinglesLevelComplete : AppRoute

  @Serializable
  data object MelodiesLevelComplete : AppRoute

  @Serializable
  data object SinglesSetup : AppRoute

  @Serializable
  data class SinglesPlay(val levelId: String) : AppRoute

  @Serializable
  data object MelodiesSetup : AppRoute

  @Serializable
  data object MelodiesPlay : AppRoute
}
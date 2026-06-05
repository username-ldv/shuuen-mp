package ldv.shuuen.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

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
  data object SinglesPlay : AppRoute

  @Serializable
  data object MelodiesSetup : AppRoute

  @Serializable
  data object MelodiesPlay : AppRoute
}
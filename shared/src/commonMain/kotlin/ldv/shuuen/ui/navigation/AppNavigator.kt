package ldv.shuuen.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import io.github.aakira.napier.Napier

class AppNavigator {
  val backStack = mutableStateListOf<AppRoute>(AppRoute.MainMenu)

  fun navigateTo(destination: AppRoute) {
    Napier.v { "Navigate happened" }
    backStack.add(destination)
  }

  fun navigateBack() {
    Napier.v { "Navigate back triggered $backStack" }
    if (backStack.size > 1) backStack.removeLastOrNull()
    Napier.v { "After navigate back triggered $backStack" }
  }
}
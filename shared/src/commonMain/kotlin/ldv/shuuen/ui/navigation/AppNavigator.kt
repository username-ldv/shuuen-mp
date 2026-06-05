package ldv.shuuen.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class AppNavigator {
  val backStack = mutableStateListOf<AppRoute>(AppRoute.MainMenu)

  fun navigateTo(destination: AppRoute) {
    backStack.add(destination)
  }

  fun navigateBack() {
    if (backStack.size > 1) backStack.removeLastOrNull()
  }
}
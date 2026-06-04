package ldv.shuuen.ui.navigation

import androidx.navigation3.runtime.NavBackStack

class AppNavigator {
  private var backStack: NavBackStack<AppRoute>? = null

  fun bind(backStack: NavBackStack<AppRoute>) {
    this.backStack = backStack
  }

  fun navigateTo(route: AppRoute) {
    backStack?.add(route)
  }

  fun navigateBack(): Boolean {
    val boundBackStack = backStack ?: return false
    if (boundBackStack.size > 1) {
      boundBackStack.removeAt(boundBackStack.lastIndex)
      return true
    }
    return false
  }
}
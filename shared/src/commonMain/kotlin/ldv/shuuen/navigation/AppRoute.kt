package ldv.shuuen.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppRoute {
    MainMenu,
    Singles,
    Settings,
}

class AppNavigator {
    private val backStack = mutableListOf(AppRoute.MainMenu)

    var currentRoute: AppRoute by mutableStateOf(AppRoute.MainMenu)
        private set

    fun navigateTo(route: AppRoute) {
        backStack += route
        currentRoute = route
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
            currentRoute = backStack.last()
        }
    }
}

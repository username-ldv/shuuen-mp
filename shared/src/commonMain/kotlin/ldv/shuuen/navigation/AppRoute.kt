package ldv.shuuen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.serializer
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute : NavKey {
    @Serializable
    data object MainMenu : AppRoute

    @Serializable
    data object Singles : AppRoute

    @Serializable
    data object Settings : AppRoute
}

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

@Composable
fun rememberAppNavBackStack(vararg elements: AppRoute): NavBackStack<AppRoute> =
    rememberSerializable(serializer = serializer()) {
        NavBackStack(*elements)
    }

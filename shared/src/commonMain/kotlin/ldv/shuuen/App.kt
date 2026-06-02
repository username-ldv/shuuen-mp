package ldv.shuuen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.ui.NavDisplay
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.navigation.AppRoute
import ldv.shuuen.navigation.rememberAppNavBackStack
import ldv.shuuen.ui.theme.ShuuenTheme
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.context.GlobalContext

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    val koin = remember { GlobalContext.get() }
    val navigator = remember { koin.get<AppNavigator>() }
    val backStack = rememberAppNavBackStack(AppRoute.MainMenu)

    SideEffect {
        navigator.bind(backStack)
    }

    ShuuenTheme(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            modifier = Modifier.fillMaxSize(),
            backStack = backStack,
            onBack = { navigator.navigateBack() },
            entryProvider = koinEntryProvider(),
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
            },
            predictivePopTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
            },
        )
    }
}

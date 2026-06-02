package ldv.shuuen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ldv.shuuen.navigation.AppNavigator
import ldv.shuuen.navigation.AppRoute
import ldv.shuuen.screens.MainMenuScreen
import ldv.shuuen.screens.SettingsScreen
import ldv.shuuen.singles.SinglesScreen
import ldv.shuuen.singles.SinglesViewModel
import ldv.shuuen.ui.theme.ShuuenTheme
import org.koin.core.context.GlobalContext

@Composable
fun App() {
    val koin = remember { GlobalContext.get() }
    val navigator = remember { koin.get<AppNavigator>() }

    ShuuenTheme(modifier = Modifier.fillMaxSize()) {
        when (navigator.currentRoute) {
            AppRoute.MainMenu -> MainMenuScreen(
                onOpenSingles = { navigator.navigateTo(AppRoute.Singles) },
                onOpenSettings = { navigator.navigateTo(AppRoute.Settings) },
            )

            AppRoute.Singles -> SinglesScreen(
                viewModel = remember { koin.get<SinglesViewModel>() },
                onNavigateBack = navigator::navigateBack,
            )

            AppRoute.Settings -> SettingsScreen(onNavigateBack = navigator::navigateBack)
        }
    }
}

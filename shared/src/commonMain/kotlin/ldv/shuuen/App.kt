package ldv.shuuen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ldv.shuuen.di.commonModule
import ldv.shuuen.di.platformModule
import ldv.shuuen.ui.navigation.NavigationRoot
import ldv.shuuen.ui.theme.ShuuenTheme
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
  KoinApplication(configuration = koinConfiguration {
    modules(listOf(commonModule, platformModule))
  }) {
    ShuuenTheme(modifier = Modifier.fillMaxSize()) {
      NavigationRoot()
    }
  }
}

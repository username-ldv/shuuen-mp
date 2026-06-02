package ldv.shuuen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ldv.shuuen.di.initShuuenKoin

fun main() = application {
  initShuuenKoin(desktopPlatformModules())

  Window(
    onCloseRequest = ::exitApplication,
    title = "Shuuen",
  ) {
    App()
  }
}

package ldv.shuuen

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import ldv.shuuen.logging.Utf8StdoutHandler

fun main() {
  Napier.base(DebugAntilog(handler = listOf(Utf8StdoutHandler())))

  application {
    Window(
      onCloseRequest = ::exitApplication,
      title = "Shuuen",
    ) {
      App()
    }
  }
}

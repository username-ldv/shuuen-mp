package ldv.shuuen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ldv.shuuen.di.initShuuenKoin

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    initShuuenKoin(androidPlatformModules(this))

    setContent {
      App()
    }
  }
}
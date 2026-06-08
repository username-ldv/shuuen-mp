package ldv.shuuen.ui.screens.training.single.play

import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier

class SinglesPlayScreenViewModel(levelId: String) : ViewModel() {
  init {
    Napier.v { "Started level with id: $levelId" }
  }
}
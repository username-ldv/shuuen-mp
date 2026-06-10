package ldv.shuuen.ui.screens.training.single.level_select

import androidx.lifecycle.ViewModel
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository

class SinglesLevelSelectScreenViewModel(levelRepository: SinglesLocalLevelRepository) :
  ViewModel() {
  val levels = levelRepository.getLevels()

}
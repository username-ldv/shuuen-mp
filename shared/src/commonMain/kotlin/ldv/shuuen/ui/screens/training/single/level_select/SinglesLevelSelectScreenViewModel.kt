package ldv.shuuen.ui.screens.training.single.level_select

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.map
import ldv.shuuen.domain.repository.SettingsRepository

class SinglesLevelSelectScreenViewModel(settingsRepository: SettingsRepository): ViewModel() {
  val levels = settingsRepository.settings.map { it.singlesLevels }
}
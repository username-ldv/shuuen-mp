package ldv.shuuen.ui.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

actual val transitions: Transitions = Transitions(
  { slideInHorizontally(initialOffsetX = { it }) togetherWith slideOutHorizontally(targetOffsetX = { -it }) },
  {
    slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
      targetOffsetX = { it })
  },
  {
    slideInHorizontally(initialOffsetX = { -it }) togetherWith slideOutHorizontally(
      targetOffsetX = { it })
  })

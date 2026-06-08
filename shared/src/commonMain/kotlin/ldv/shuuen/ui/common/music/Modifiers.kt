package ldv.shuuen.ui.common.music

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.withClearFocusOnTap(
  focusManager: FocusManager,
  after: AwaitPointerEventScope.() -> Unit = {}
): Modifier = pointerInput(focusManager) {
  awaitEachGesture {
    awaitFirstDown(pass = PointerEventPass.Initial)
    focusManager.clearFocus()
    after()
  }
}
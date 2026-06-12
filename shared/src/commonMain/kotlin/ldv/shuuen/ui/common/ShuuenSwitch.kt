package ldv.shuuen.ui.common

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Monotone switch: inverted white track when checked. */
@Composable
fun ShuuenSwitch(
  checked: Boolean,
  onCheckedChange: ((Boolean) -> Unit)? = {},
) {
  Switch(
    checked = checked,
    onCheckedChange = onCheckedChange,
    colors = SwitchDefaults.colors(
      checkedThumbColor = ShuuenUi.OnInverse,
      checkedTrackColor = ShuuenUi.Inverse,
      uncheckedThumbColor = ShuuenUi.Muted,
      uncheckedTrackColor = Color.White.copy(alpha = 0.07f),
      uncheckedBorderColor = Color.Transparent,
    ),
  )
}

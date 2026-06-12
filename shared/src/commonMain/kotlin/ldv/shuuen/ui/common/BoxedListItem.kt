package ldv.shuuen.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable


@Serializable
data class BoxedListItemState(val active: Boolean, val label: String)

@Composable
fun BoxedListItem(
  label: String,
  active: Boolean,
  itemSize: Dp = 50.dp,
  onClicked: () -> Unit = {},
) {
  Surface(
    color = if (active) ShuuenUi.Inverse else Color.White.copy(alpha = 0.05f),
    contentColor = if (active) ShuuenUi.OnInverse else ShuuenUi.Muted,
    modifier = Modifier.size(itemSize),
    shape = MaterialTheme.shapes.small,
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
    onClick = onClicked
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        fontSize = 15.sp,
      )
    }
  }
}

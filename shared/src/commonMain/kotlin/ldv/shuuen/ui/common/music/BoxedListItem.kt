package ldv.shuuen.ui.common.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class BoxedListItemState(val active: Boolean, val label: String)

@Composable
fun BoxedListItem(
  label: String,
  active: Boolean,
  itemSize: Dp = 50.dp,
  onClicked: () -> Unit = {},
) {
  val surfaceColor = if (active) {
    MaterialTheme.colorScheme.surfaceVariant
  } else {
    MaterialTheme.colorScheme.surfaceContainer
  }
  Surface(
    color = surfaceColor,
    modifier = Modifier.size(itemSize),
    shape = RoundedCornerShape(4.dp),
    shadowElevation = 4.dp,
    onClick = onClicked
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = label,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp
      )
    }
  }
}
package ldv.shuuen.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun PillControl(
  text: String,
  modifier: Modifier = Modifier.Companion,
  selected: Boolean = false,
  leadingIcon: ImageVector? = null,
  trailingCheck: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  SoftControl(
    modifier = modifier,
    selected = selected,
    onClick = onClick,
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = if (selected) ShuuenUi.Mint else ShuuenUi.Lavender,
        modifier = Modifier.size(22.dp),
      )
    }
    Text(
      text = text,
      color = if (selected) ShuuenUi.Text else ShuuenUi.Muted,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.weight(1f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    if (trailingCheck) {
      Box(
        modifier = Modifier.size(24.dp).clip(CircleShape).background(ShuuenUi.Mint),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = null,
          tint = Color.Black,
          modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}
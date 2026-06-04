package ldv.shuuen.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconBubble(
  icon: ImageVector,
  modifier: Modifier = Modifier.Companion,
  tint: Color = ShuuenUi.Lavender,
  size: Dp = 58.dp,
) {
  Box(
    modifier = modifier.size(size).clip(CircleShape)
      .background(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)))
      .border(1.dp, ShuuenUi.Border, CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = tint,
      modifier = Modifier.size(size * 0.46f),
    )
  }
}
package ldv.shuuen.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashedAddButton(
  text: String,
  modifier: Modifier = Modifier.Companion,
) {
  Box(
    modifier = modifier.fillMaxWidth().height(52.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize()) {
      drawRoundRect(
        color = Color.White.copy(alpha = 0.28f),
        topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
        size = Size(size.width - 2.dp.toPx(), size.height - 2.dp.toPx()),
        cornerRadius = CornerRadius(14.dp.toPx()),
        style = Stroke(
          width = 1.2.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(9.dp.toPx(), 8.dp.toPx())),
        ),
      )
    }
    Text(
      text = "+  $text",
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.titleSmall.copy(
        letterSpacing = 3.sp,
        fontWeight = FontWeight.SemiBold,
      ),
    )
  }
}

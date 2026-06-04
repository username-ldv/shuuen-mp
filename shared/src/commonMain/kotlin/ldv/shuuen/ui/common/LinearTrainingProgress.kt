package ldv.shuuen.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun LinearTrainingProgress(
  progress: Float,
  modifier: Modifier = Modifier.Companion,
  color: Color = ShuuenUi.Lavender,
) {
  Canvas(
    modifier = modifier.fillMaxWidth().height(8.dp).padding(horizontal = 4.dp),
  ) {
    val stroke = 7.dp.toPx()
    val y = size.height / 2f
    drawLine(
      color = Color.White.copy(alpha = 0.14f),
      start = Offset(0f, y),
      end = Offset(size.width, y),
      strokeWidth = stroke,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = color,
      start = Offset(0f, y),
      end = Offset(size.width * progress.coerceIn(0f, 1f), y),
      strokeWidth = stroke,
      cap = StrokeCap.Round,
    )
  }
}
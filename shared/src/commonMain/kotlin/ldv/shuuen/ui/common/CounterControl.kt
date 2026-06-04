package ldv.shuuen.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun CounterControl(
  value: String,
  modifier: Modifier = Modifier.Companion,
) {
  Row(
    modifier = modifier.fillMaxWidth().height(58.dp).clip(ShuuenUi.PillShape)
      .border(1.dp, ShuuenUi.Border, ShuuenUi.PillShape),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CounterPart("-")
    Box(
      modifier = Modifier.weight(1f).fillMaxHeight()
        .border(1.dp, ShuuenUi.Border.copy(alpha = 0.45f)),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = value,
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.headlineLarge,
      )
    }
    CounterPart("+")
  }
}

@Composable
private fun RowScope.CounterPart(text: String) {
  Box(
    modifier = Modifier.weight(0.42f).fillMaxHeight(),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.headlineLarge,
    )
  }
}

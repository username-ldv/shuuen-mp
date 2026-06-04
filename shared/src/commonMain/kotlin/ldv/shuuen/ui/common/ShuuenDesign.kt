package ldv.shuuen.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


// question: where would these be used?

@Composable
private fun RangeHandle(modifier: Modifier) {
  Box(
    modifier = modifier.size(width = 30.dp, height = 74.dp).clip(RoundedCornerShape(15.dp))
      .background(Color(0xFFEAE5FF)).border(1.dp, ShuuenUi.Lavender, RoundedCornerShape(15.dp)),
    contentAlignment = Alignment.Center,
  ) {
    Text("III", color = Color.Black, style = MaterialTheme.typography.labelLarge)
  }
}

@Composable
fun BoxScope.CenterPlayGlyph() {
  Icon(
    imageVector = Icons.Rounded.PlayArrow,
    contentDescription = null,
    tint = ShuuenUi.Text,
    modifier = Modifier.align(Alignment.Center).size(70.dp),
  )
}

@Composable
fun KeyboardIcon(tint: Color = ShuuenUi.Lavender, modifier: Modifier = Modifier) {
  Icon(
    imageVector = Icons.Rounded.Keyboard,
    contentDescription = null,
    tint = tint,
    modifier = modifier,
  )
}

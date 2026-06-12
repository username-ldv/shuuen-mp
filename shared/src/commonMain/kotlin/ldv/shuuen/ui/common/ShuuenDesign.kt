package ldv.shuuen.ui.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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
fun KeyboardIcon(tint: Color = ShuuenUi.Muted, modifier: Modifier = Modifier) {
  Icon(
    imageVector = Icons.Rounded.Keyboard,
    contentDescription = null,
    tint = tint,
    modifier = modifier,
  )
}

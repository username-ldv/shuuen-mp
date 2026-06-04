package ldv.shuuen.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SoftControl(
  modifier: Modifier = Modifier.Companion,
  selected: Boolean = false,
  onClick: (() -> Unit)? = null,
  content: @Composable RowScope.() -> Unit,
) {
  val shape = MaterialTheme.shapes.extraSmall
  Row(
    modifier = modifier.clip(shape)
      .background(if (selected) Color(0x331E4A3C) else ShuuenUi.PanelSoft).border(
        width = 1.dp,
        color = if (selected) ShuuenUi.Mint else ShuuenUi.Border,
        shape = shape,
      ).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier.Companion)
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(
      space = 4.dp, alignment = Alignment.CenterHorizontally
    ),
    content = content,
  )
}
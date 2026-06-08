package ldv.shuuen.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrimaryCta(
  text: String,
  modifier: Modifier = Modifier,
  icon: ImageVector = Icons.Rounded.PlayArrow,
  onClick: () -> Unit,
) {
  Row(
    modifier = modifier.fillMaxWidth().height(68.dp).clip(RoundedCornerShape(14.dp))
      .background(ShuuenUi.Mint).clickable(onClick = onClick).padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = Color.Black,
      modifier = Modifier.size(30.dp),
    )
    Text(
      text = text,
      color = Color.Black,
      style = MaterialTheme.typography.titleLarge.copy(
        letterSpacing = 5.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
  }
}
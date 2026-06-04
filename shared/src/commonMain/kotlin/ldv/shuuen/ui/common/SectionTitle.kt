package ldv.shuuen.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SectionTitle(
  icon: ImageVector,
  title: String,
  subtitle: String,
  tint: Color = ShuuenUi.Lavender,
  trailing: (@Composable RowScope.() -> Unit)? = null,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val compact = maxWidth < 420.dp && trailing != null

    Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        IconBubble(icon = icon, tint = tint, size = 50.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = title,
            color = ShuuenUi.Text,
            style = MaterialTheme.typography.titleLarge.copy(
              letterSpacing = ShuuenUi.titlesSpacing,
              fontWeight = FontWeight.Bold,
            ),
            maxLines = if (compact) 1 else 2,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            text = subtitle,
            color = ShuuenUi.Muted,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
          )
        }
        if (trailing != null && !compact) {
          Row(content = trailing)
        }
      }
      if (trailing != null && compact) {
        Row(modifier = Modifier.fillMaxWidth(), content = trailing)
      }
    }
  }
}
package ldv.shuuen.ui.common

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// todo: can it be moved somewhere better? like the MaterialTheme?
object ShuuenUi {
  val Panel = Color(0xE6121212)
  val PanelHigh = Color(0xF21A1A1A)
  val PanelSoft = Color(0x661F1F1F)
  val Border = Color.White.copy(alpha = 0.18f)
  val BorderStrong = Color.White.copy(alpha = 0.32f)
  val Text = Color(0xFFF4F4F4)
  val Muted = Color(0xFF9C9CA4)
  val Dim = Color(0xFF686870)
  val Mint = Color(0xFFBFE8D8)
  val MintBright = Color(0xFF79F1C9)
  val Lavender = Color(0xFFC7B1FF)
  val Gold = Color(0xFFE9CC83)
  val Red = Color(0xFFFF5B57)
  val Green = Color(0xFF52E58A)

  val PillShape = RoundedCornerShape(50)

  val titlesSpacing = 2.sp
}
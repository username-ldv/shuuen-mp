package ldv.shuuen.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.music.PianoKeyIndication
import ldv.shuuen.ui.music.PianoKeyboard
import ldv.shuuen.ui.music.PianoKeyboardDefaults

object ShuuenUi {
  val Background = Color.Black
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

@Composable
fun StaticScreenFrame(
  modifier: Modifier = Modifier,
  contentPadding: Dp = 16.dp,
  maxWidth: Dp = 560.dp,
  topBar: @Composable () -> Unit = {},
  content: @Composable ColumnScope.() -> Unit,
) {
  Box(
    modifier = modifier.fillMaxSize().background(
      Brush.verticalGradient(
        colors = listOf(
          Color.Black,
          Color(0xFF050505),
          Color.Black,
        ),
      ),
    ),
    contentAlignment = Alignment.TopCenter,
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Transparent,
      contentColor = ShuuenUi.Text,
      topBar = topBar,
    ) { innerPadding ->
      Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.TopCenter,
      ) {
        Column(
          modifier = Modifier.widthIn(max = maxWidth).fillMaxWidth().fillMaxHeight()
            .navigationBarsPadding().verticalScroll(rememberScrollState())
            .padding(horizontal = contentPadding, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          content = content,
        )
      }
    }
  }
}

enum class ShuuenTopAppBarType(val height: Dp) {
  Simple(50.dp), Labeled(TopAppBarDefaults.TopAppBarExpandedHeight)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShuuenTopAppBar(
  modifier: Modifier = Modifier,
  title: String? = null,
  subtitle: String? = null,
  onBack: (() -> Unit)? = null,
  trailingIcon: ImageVector? = null,
  onTrailingClick: (() -> Unit)? = null,
  type: ShuuenTopAppBarType = ShuuenTopAppBarType.Simple
) {
  BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    val compact = maxWidth < 430.dp
    val titleSize = if (compact) 25.sp else 32.sp

    CenterAlignedTopAppBar(
      expandedHeight = type.height,
      title = {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
          if (title != null) {
            Text(
              text = title,
              color = ShuuenUi.Text,
              style = MaterialTheme.typography.displayMedium.copy(
                fontSize = titleSize,
                lineHeight = if (compact) 31.sp else 38.sp,
                letterSpacing = ShuuenUi.titlesSpacing,
                fontWeight = FontWeight.SemiBold,
              ),
              textAlign = TextAlign.Center,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
          if (subtitle != null) {
            Text(
              text = subtitle,
              color = ShuuenUi.Muted,
              style = if (compact) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
              textAlign = TextAlign.Center,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }
      },
      modifier = Modifier.fillMaxWidth(),
      navigationIcon = {
        if (onBack != null) {
          CircleIconButton(
            icon = Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = "Back",
            onClick = onBack,
          )
        }
      },
      actions = {
        if (trailingIcon != null) {
          CircleIconButton(
            icon = trailingIcon,
            contentDescription = null,
            onClick = onTrailingClick ?: {},
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = Color.Transparent,
        scrolledContainerColor = Color.Transparent,
        navigationIconContentColor = ShuuenUi.Text,
        titleContentColor = ShuuenUi.Text,
        actionIconContentColor = ShuuenUi.Text,
      ),
    )
  }
}

@Composable
fun GlassPanel(
  modifier: Modifier = Modifier,
  borderColor: Color = ShuuenUi.Border,
  content: @Composable ColumnScope.() -> Unit,
) {
  Surface(
    modifier = modifier.fillMaxWidth(),
    color = ShuuenUi.Panel,
    contentColor = ShuuenUi.Text,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, borderColor),
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
      content = content,
    )
  }
}

@Composable
fun SoftControl(
  modifier: Modifier = Modifier,
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
      ).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(horizontal = 8.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(
      space = 4.dp, alignment = Alignment.CenterHorizontally
    ),
    content = content,
  )
}

@Composable
fun PillControl(
  text: String,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  leadingIcon: ImageVector? = null,
  trailingCheck: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  SoftControl(
    modifier = modifier,
    selected = selected,
    onClick = onClick,
  ) {
    if (leadingIcon != null) {
      Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = if (selected) ShuuenUi.Mint else ShuuenUi.Lavender,
        modifier = Modifier.size(22.dp),
      )
    }
    Text(
      text = text,
      color = if (selected) ShuuenUi.Text else ShuuenUi.Muted,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.weight(1f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    if (trailingCheck) {
      Box(
        modifier = Modifier.size(24.dp).clip(CircleShape).background(ShuuenUi.Mint),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.Rounded.Check,
          contentDescription = null,
          tint = Color.Black,
          modifier = Modifier.size(16.dp),
        )
      }
    }
  }
}

@Composable
fun IconBubble(
  icon: ImageVector,
  modifier: Modifier = Modifier,
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

@Composable
fun CircleIconButton(
  icon: ImageVector,
  contentDescription: String?,
  onClick: () -> Unit,
) {
  IconButton(onClick = onClick) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
//      tint = ShuuenUi.Text,
    )
  }
}

@Composable
fun PrimaryCta(
  text: String,
  modifier: Modifier = Modifier,
  onClick: () -> Unit,
) {
  Row(
    modifier = modifier.fillMaxWidth().height(68.dp).clip(RoundedCornerShape(14.dp))
      .background(ShuuenUi.Mint).clickable(onClick = onClick).padding(horizontal = 24.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center,
  ) {
    Icon(
      imageVector = Icons.Rounded.PlayArrow,
      contentDescription = null,
      tint = Color.Black,
      modifier = Modifier.size(30.dp),
    )
    Spacer(Modifier.width(18.dp))
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

@Composable
fun LinearTrainingProgress(
  progress: Float,
  modifier: Modifier = Modifier,
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

@Composable
fun DashedAddButton(
  text: String,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier.fillMaxWidth().height(54.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(Modifier.fillMaxSize()) {
      drawRoundRect(
        color = ShuuenUi.Mint.copy(alpha = 0.9f),
        topLeft = Offset(1.dp.toPx(), 1.dp.toPx()),
        size = Size(size.width - 2.dp.toPx(), size.height - 2.dp.toPx()),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
        style = Stroke(
          width = 1.4.dp.toPx(),
          pathEffect = PathEffect.dashPathEffect(floatArrayOf(9.dp.toPx(), 8.dp.toPx())),
        ),
      )
    }
    Text(
      text = "+  $text",
      color = ShuuenUi.Mint,
      style = MaterialTheme.typography.titleSmall.copy(
        letterSpacing = 3.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
  }
}

@Composable
fun CounterControl(
  value: String,
  modifier: Modifier = Modifier,
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

@Composable
fun RangeKeyboardStrip(
  modifier: Modifier = Modifier,
  firstSelected: Int = 12,
  lastSelectedExclusive: Int = 37,
) {
  val keyCount = 12
  val selectedRange = firstSelected until lastSelectedExclusive
  PianoKeyboard(
    modifier = Modifier.fillMaxWidth().aspectRatio(PianoKeyboardDefaults.aspectRatio(keyCount)),
    keyCount = keyCount,
//      idleKeyColors = List(keyCount) { index ->
//        when {
//          index !in selectedRange -> if (PianoKeyboardDefaults.isBlackKey(index)) Color(0xFF151515) else Color(
//            0xFF292929
//          )
//
//          PianoKeyboardDefaults.isBlackKey(index) -> Color(0xFF34313F)
//          else -> Color(0xFFE2D8FF)
//        }
//      },
  )
//    RangeHandle(Modifier.align(Alignment.CenterStart).padding(start = 0.dp))
//    RangeHandle(Modifier.align(Alignment.CenterEnd).padding(end = 86.dp))
}

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

package ldv.shuuen.ui.screens.context

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.common.DashedAddButton
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.SectionTitle
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.inputs.PianoKeyboard
import ldv.shuuen.ui.common.music.inputs.PianoKeyboardDefaults

@Composable
fun ContextScreen(onNavigateBack: () -> Unit) {
  var selectedTab by rememberSaveable { mutableStateOf(ContextTab.Drone) }

  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "CONTEXT",
        subtitle = "Configure the listening context.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled,
      )
    },
  ) {
    ContextTabs(
      selectedTab = selectedTab,
      onSelectTab = { selectedTab = it },
    )

    when (selectedTab) {
      ContextTab.Drone -> DroneContextTab()
      ContextTab.Cadence -> CadenceContextTab()
    }

    PrimaryCta(
      text = "SAVE CONTEXT",
      onClick = onNavigateBack,
      modifier = Modifier.padding(bottom = 18.dp),
    )
  }
}

private enum class ContextTab {
  Drone,
  Cadence,
}

private data class ContextNode(
  val title: String,
  val subtitle: String,
  val function: String? = null,
  val notes: List<String>,
  val previewLabel: String,
  val questionLabel: String,
  val questionCount: String,
)

@Composable
private fun ContextTabs(
  selectedTab: ContextTab,
  onSelectTab: (ContextTab) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(58.dp)
      .clip(ShuuenUi.PillShape)
      .border(1.dp, ShuuenUi.BorderStrong, ShuuenUi.PillShape)
      .padding(4.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    ContextTabButton(
      text = "DRONE",
      selected = selectedTab == ContextTab.Drone,
      onClick = { onSelectTab(ContextTab.Drone) },
      modifier = Modifier.weight(1f),
    )
    ContextTabButton(
      text = "CADENCE",
      selected = selectedTab == ContextTab.Cadence,
      onClick = { onSelectTab(ContextTab.Cadence) },
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun ContextTabButton(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .fillMaxHeight()
      .clip(ShuuenUi.PillShape)
      .background(if (selected) ShuuenUi.Mint else Color.Transparent)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = if (selected) Color.Black else ShuuenUi.Muted,
      style = MaterialTheme.typography.titleMedium.copy(
        letterSpacing = 4.sp,
        fontWeight = FontWeight.Bold,
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun DroneContextTab() {
  ContextSequencePanel(
    title = "DRONE SEQUENCE",
    subtitle = "Build the drone progression used during training.",
    countLabel = "2 nodes",
  ) {
    ContextNodeCard(
      number = 1,
      node = ContextNode(
        title = "NODE 1",
        subtitle = "Plays before Node 2",
        notes = listOf("C3", "G3", "C4"),
        previewLabel = "Preview drone",
        questionLabel = "QUESTIONS BEFORE NEXT",
        questionCount = "5",
      ),
    )
    ContextNodeCard(
      number = 2,
      node = ContextNode(
        title = "NODE 2",
        subtitle = "Plays before restart",
        notes = listOf("F3", "C4"),
        previewLabel = "Preview drone",
        questionLabel = "QUESTIONS BEFORE RESTART",
        questionCount = "8",
      ),
    )
    DashedAddButton("ADD NODE")
  }

  ContextInfoPanel(
    text = "Each node can contain multiple notes across octaves.\nAfter the last node, the sequence returns to the first node.",
    detail = "Choose notes across octaves. Opens a scrollable keyboard picker.",
    markerCount = 2,
  )
}

@Composable
private fun CadenceContextTab() {
  ContextSequencePanel(
    title = "CADENCE SEQUENCE",
    subtitle = "Build the cadence progression used during training.",
    countLabel = "3 nodes",
  ) {
    PresetProgressionControls()
    PreviewFullSequence()
    ContextNodeCard(
      number = 1,
      node = ContextNode(
        title = "NODE 1",
        subtitle = "Plays before Node 2",
        function = "I",
        notes = listOf("C3", "E3", "G3"),
        previewLabel = "Preview cadence",
        questionLabel = "QUESTIONS BEFORE NEXT",
        questionCount = "4",
      ),
    )
    ContextNodeCard(
      number = 2,
      node = ContextNode(
        title = "NODE 2",
        subtitle = "Plays before Node 3",
        function = "IV",
        notes = listOf("F3", "A3", "C4"),
        previewLabel = "Preview cadence",
        questionLabel = "QUESTIONS BEFORE NEXT",
        questionCount = "4",
      ),
    )
    ContextNodeCard(
      number = 3,
      node = ContextNode(
        title = "NODE 3",
        subtitle = "Plays before restart",
        function = "V",
        notes = listOf("G3", "B3", "D4"),
        previewLabel = "Preview cadence",
        questionLabel = "QUESTIONS BEFORE RESTART",
        questionCount = "6",
      ),
    )
    DashedAddButton("ADD NODE")
  }

  ContextInfoPanel(
    text = "Presets create common cadence progressions to get you started.\nEach node can be edited individually to fit your training goals.\nAfter the last node, the sequence returns to the first node.",
    markerCount = 4,
  )
}

@Composable
private fun ContextSequencePanel(
  title: String,
  subtitle: String,
  countLabel: String,
  content: @Composable () -> Unit,
) {
  GlassPanel(borderColor = ShuuenUi.BorderStrong) {
    SectionTitle(
      icon = Icons.Rounded.GraphicEq,
      title = title,
      subtitle = subtitle,
      trailing = {
        SoftControl(modifier = Modifier.width(92.dp)) {
          Text(
            text = countLabel,
            color = ShuuenUi.Mint,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
          )
        }
      },
    )
    content()
  }
}

@Composable
private fun PresetProgressionControls() {
  SoftControl(modifier = Modifier.fillMaxWidth()) {
    Icon(
      imageVector = Icons.Rounded.Edit,
      contentDescription = null,
      tint = ShuuenUi.Lavender,
      modifier = Modifier.size(22.dp),
    )
    Text(
      text = "Preset progression",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.weight(1f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      text = "I-IV-V-I",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
    )
    Icon(
      Icons.Rounded.ChevronRight,
      contentDescription = null,
      tint = ShuuenUi.Muted,
      modifier = Modifier.size(24.dp),
    )
  }
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    listOf("I-IV-V-I", "ii-V-I", "I-vi-ii-V").forEachIndexed { index, text ->
      SmallPill(
        text = text,
        selected = index == 0,
        modifier = Modifier.weight(1f),
      )
    }
  }
}

@Composable
private fun PreviewFullSequence() {
  SoftControl(modifier = Modifier.fillMaxWidth()) {
    PlayBubble()
    Text(
      text = "Preview full sequence",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.weight(1f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    MiniWaveform(Modifier.width(126.dp).height(30.dp), pieces = 4)
    Icon(
      Icons.Rounded.ChevronRight,
      contentDescription = null,
      tint = ShuuenUi.Muted,
      modifier = Modifier.size(24.dp),
    )
  }
}

@Composable
private fun ContextNodeCard(
  number: Int,
  node: ContextNode,
) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = ShuuenUi.PanelSoft,
    contentColor = ShuuenUi.Text,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, ShuuenUi.Border),
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
      Icon(
        imageVector = Icons.Rounded.Delete,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.align(Alignment.TopEnd).size(30.dp),
      )
      Row(
        modifier = Modifier.fillMaxWidth().padding(end = 32.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        NumberedWaveIcon(number = number)
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
              text = node.title,
              color = ShuuenUi.Text,
              style = MaterialTheme.typography.titleLarge.copy(
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
              ),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = node.subtitle,
              color = ShuuenUi.Muted,
              style = MaterialTheme.typography.bodyLarge,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }

          NodeChips(function = node.function, notes = node.notes)
          NodeActionRow(label = "Choose notes", icon = Icons.Rounded.Keyboard, trailing = true)
          NodeActionRow(label = node.previewLabel, play = true) {
            MiniWaveform(Modifier.width(120.dp).height(28.dp), pieces = 3)
          }
          InlineCounter(label = node.questionLabel, value = node.questionCount)
        }
      }
    }
  }
}

@Composable
private fun NumberedWaveIcon(number: Int) {
  Box(contentAlignment = Alignment.TopStart) {
    IconBubble(Icons.Rounded.GraphicEq, tint = ShuuenUi.Lavender, size = 56.dp)
    Box(
      modifier = Modifier
        .size(24.dp)
        .clip(CircleShape)
        .background(Color(0xFF1A1A1A))
        .border(1.dp, ShuuenUi.BorderStrong, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = number.toString(),
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
      )
    }
  }
}

@Composable
private fun NodeChips(
  function: String?,
  notes: List<String>,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val compact = maxWidth < 360.dp

    if (function == null || compact) {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ChipGroup("NOTES", notes)
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(22.dp),
      ) {
        ChipGroup("FUNCTION", listOf(function), Modifier.weight(0.7f))
        ChipGroup("NOTES", notes, Modifier.weight(1.5f))
      }
    }
  }
}

@Composable
private fun ChipGroup(
  label: String,
  values: List<String>,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
    Text(
      text = label,
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.labelSmall.copy(
        letterSpacing = 2.sp,
        fontWeight = FontWeight.Bold,
      ),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      values.forEach { value ->
        SmallPill(text = value, selected = true, modifier = Modifier.width(58.dp))
      }
    }
  }
}

@Composable
private fun NodeActionRow(
  label: String,
  icon: ImageVector? = null,
  trailing: Boolean = false,
  play: Boolean = false,
  content: @Composable RowScope.() -> Unit = {},
) {
  SoftControl(modifier = Modifier.fillMaxWidth()) {
    if (play) {
      PlayBubble()
    } else if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = ShuuenUi.Text,
        modifier = Modifier.size(24.dp),
      )
    }
    Text(
      text = label,
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.weight(1f),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    content()
    if (trailing) {
      Icon(
        Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(24.dp),
      )
    }
  }
}

@Composable
private fun InlineCounter(
  label: String,
  value: String,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = label,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.8.sp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.weight(1f),
    )
    CompactCounter(value = value, modifier = Modifier.weight(1.15f))
  }
}

@Composable
private fun CompactCounter(
  value: String,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier
      .height(38.dp)
      .clip(ShuuenUi.PillShape)
      .border(1.dp, ShuuenUi.Border, ShuuenUi.PillShape),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    CounterPiece("-")
    CounterPiece(value, Modifier.weight(1.35f))
    CounterPiece("+")
  }
}

@Composable
private fun RowScope.CounterPiece(
  text: String,
  modifier: Modifier = Modifier.weight(1f),
) {
  Box(
    modifier = modifier.fillMaxHeight(),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = text, color = ShuuenUi.Text, style = MaterialTheme.typography.titleLarge)
  }
}

@Composable
private fun ContextInfoPanel(
  text: String,
  detail: String? = null,
  markerCount: Int,
) {
  GlassPanel {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Icon(
        Icons.Rounded.Info,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(30.dp)
      )
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Text(
          text = text,
          color = ShuuenUi.Muted,
          style = MaterialTheme.typography.bodyLarge,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          MiniRangeKeyboard(
            markerCount = markerCount,
            modifier = Modifier.weight(if (detail == null) 1f else 1.5f),
          )
          if (detail != null) {
            Text(
              text = detail,
              color = ShuuenUi.Muted,
              style = MaterialTheme.typography.bodyMedium,
              modifier = Modifier.weight(1f),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun MiniRangeKeyboard(
  markerCount: Int,
  modifier: Modifier = Modifier,
) {
  val keyCount = 24
  val selectedStart = 4
  val selected = selectedStart until (selectedStart + markerCount * 4).coerceAtMost(keyCount - 2)
  PianoKeyboard(
    modifier = modifier.aspectRatio(PianoKeyboardDefaults.aspectRatio(keyCount)),
    keyCount = keyCount,
    idleKeyColors = List(keyCount) { index ->
      when {
        index !in selected -> if (PianoKeyboardDefaults.isBlackKey(index)) {
          Color(0xFF151515)
        } else {
          Color(0xFF2A2A2A)
        }

        PianoKeyboardDefaults.isBlackKey(index) -> Color(0xFF4A435E)
        else -> Color(0xFFE0D6FF)
      }
    },
    borderWidth = 1.dp,
    separatorWidth = 1.dp,
    whiteKeyCornerRadius = 3.dp,
    blackKeyCornerRadius = 2.dp,
  )
}

@Composable
private fun SmallPill(
  text: String,
  selected: Boolean,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .height(30.dp)
      .clip(ShuuenUi.PillShape)
      .background(if (selected) Color(0xFFE0D6FF) else Color.Transparent)
      .border(1.dp, if (selected) Color.Transparent else ShuuenUi.Border, ShuuenUi.PillShape),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = if (selected) Color.Black else ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun PlayBubble() {
  Box(
    modifier = Modifier
      .size(36.dp)
      .clip(CircleShape)
      .background(ShuuenUi.Mint),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      imageVector = Icons.Rounded.PlayArrow,
      contentDescription = null,
      tint = Color.Black,
      modifier = Modifier.size(24.dp),
    )
  }
}

@Composable
private fun MiniWaveform(
  modifier: Modifier = Modifier,
  pieces: Int = 3,
) {
  Canvas(modifier = modifier) {
    val segmentWidth = size.width / (pieces * 5f)
    val centerY = size.height / 2f
    var x = segmentWidth

    repeat(pieces) {
      listOf(0.35f, 0.7f, 1f, 0.55f).forEach { heightFraction ->
        val lineHeight = size.height * heightFraction
        drawLine(
          color = ShuuenUi.Lavender,
          start = Offset(x, centerY - lineHeight / 2f),
          end = Offset(x, centerY + lineHeight / 2f),
          strokeWidth = 2.dp.toPx(),
          cap = StrokeCap.Round,
        )
        x += segmentWidth
      }
      if (it < pieces - 1) {
        drawLine(
          color = ShuuenUi.Muted,
          start = Offset(x, centerY),
          end = Offset(x + segmentWidth * 0.8f, centerY),
          strokeWidth = 1.dp.toPx(),
        )
        x += segmentWidth * 1.2f
      }
    }
  }
}

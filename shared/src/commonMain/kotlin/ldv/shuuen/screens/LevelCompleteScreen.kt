package ldv.shuuen.screens

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class LevelCompleteData(
  val levelTitle: String,
  val flowLabel: String,
  val key: String,
  val parameterDescription: String,
  val parameters: List<Pair<ImageVector, String>>,
)

@Composable
fun LevelCompleteScreen(
  flow: TrainingFlow,
  onNavigateBack: () -> Unit,
  onRetryLevel: () -> Unit,
  onNextLevel: () -> Unit,
) {
  val data = levelCompleteData(flow)

  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "SESSION COMPLETE",
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Share,
        type = ShuuenTopAppBarType.Simple,
      )
    },
  ) {
    CompletionTitle(data)
    ScoreHero(data)
    CompletionActions(
      onRetryLevel = onRetryLevel,
      onNextLevel = onNextLevel,
    )
    PerformanceOverview()
    LevelParameters(data)
  }
}

private fun levelCompleteData(flow: TrainingFlow): LevelCompleteData {
  return when (flow) {
    TrainingFlow.Singles -> LevelCompleteData(
      levelTitle = "Level 2 - Triad Tones",
      flowLabel = "Singles",
      key = "D Major",
      parameterDescription = "Root, third, and fifth of the triad.",
      parameters = listOf(
        Icons.Rounded.MusicNote to "3 notes",
        Icons.AutoMirrored.Rounded.HelpOutline to "20 questions",
        Icons.Rounded.Speed to "96 BPM",
        Icons.Rounded.Keyboard to "C3-C5",
        Icons.Rounded.Replay to "Random",
        Icons.Rounded.TrackChanges to "D Major",
      ),
    )

    TrainingFlow.Melodies -> LevelCompleteData(
      levelTitle = "Level 2 - Triad Motifs",
      flowLabel = "Melodies",
      key = "D Major",
      parameterDescription = "Four-note melodies built from triad tones.",
      parameters = listOf(
        Icons.Rounded.MusicNote to "4 notes",
        Icons.AutoMirrored.Rounded.HelpOutline to "20 questions",
        Icons.Rounded.Speed to "96 BPM",
        Icons.Rounded.Keyboard to "C3-C5",
        Icons.Rounded.Replay to "Random",
        Icons.Rounded.TrackChanges to "D Major",
      ),
    )
  }
}

@Composable
private fun CompletionTitle(data: LevelCompleteData) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(
      text = data.levelTitle,
      color = ShuuenUi.Lavender,
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      textAlign = TextAlign.Center,
    )
    Text(
      text = "${data.flowLabel} • ${data.key}",
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.titleMedium,
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun ScoreHero(data: LevelCompleteData) {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = ShuuenUi.Panel,
    contentColor = ShuuenUi.Text,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, ShuuenUi.Border),
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(22.dp),
    ) {
      ScoreRing()
      ScoreSummary(data, Modifier.weight(1f))
    }
  }
}

@Composable
private fun ScoreRing() {
  Box(modifier = Modifier.size(116.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.size(116.dp)) {
      val stroke = 8.dp.toPx()
      val radius = size.minDimension / 2f - stroke
      drawArc(
        color = ShuuenUi.Lavender,
        startAngle = -88f,
        sweepAngle = 245f,
        useCenter = false,
        style = Stroke(stroke, cap = StrokeCap.Round),
      )
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "68%",
        color = ShuuenUi.Lavender,
        style = MaterialTheme.typography.displayLarge.copy(fontSize = 34.sp, fontWeight = FontWeight.Bold),
      )
      Text(
        text = "SCORE",
        color = ShuuenUi.Muted,
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp, fontWeight = FontWeight.Bold),
      )
    }
  }
}

@Composable
private fun ScoreSummary(data: LevelCompleteData, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
      text = "14 / 20 CORRECT",
      color = ShuuenUi.Lavender,
      style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp, fontWeight = FontWeight.Bold),
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
    Text("${data.flowLabel} level result", color = ShuuenUi.Muted, style = MaterialTheme.typography.bodyMedium)
  }
}

@Composable
private fun PerformanceOverview() {
  GlassPanel(borderColor = ShuuenUi.BorderStrong) {
    PanelHeading("PERFORMANCE OVERVIEW")
    StatsGrid()
    PanelHeading("ACCURACY BY QUESTION RANGE")
    AccuracyRangeBar()
  }
}

@Composable
private fun StatsGrid() {
  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = Color.Transparent,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, ShuuenUi.Border),
  ) {
    Column {
      Row(modifier = Modifier.fillMaxWidth()) {
        StatCell(Icons.Rounded.TrackChanges, "68%", "ACCURACY", Modifier.weight(1f))
        StatCell(Icons.Rounded.Schedule, "2.8s", "AVG TIME", Modifier.weight(1f))
        StatCell(Icons.Rounded.LocalFireDepartment, "6", "BEST", Modifier.weight(1f))
      }
      Row(modifier = Modifier.fillMaxWidth()) {
        StatCell(Icons.Rounded.Replay, "2", "REPLAYS", Modifier.weight(1f))
        StatCell(Icons.Rounded.Close, "6", "WRONG", Modifier.weight(1f))
        StatCell(Icons.Rounded.BarChart, "", "HOTSPOTS", Modifier.weight(1f), trailing = true)
      }
    }
  }
}

@Composable
private fun RowScope.StatCell(
  icon: ImageVector,
  value: String,
  label: String,
  modifier: Modifier = Modifier,
  trailing: Boolean = false,
) {
  Column(
    modifier = modifier.height(82.dp).border(0.5.dp, ShuuenUi.Border).padding(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      Icon(icon, contentDescription = null, tint = ShuuenUi.Mint, modifier = Modifier.size(22.dp))
      if (value.isNotBlank()) {
        Text(
          value,
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.headlineLarge.copy(fontSize = 22.sp),
          maxLines = 1,
        )
      }
      if (trailing) {
        Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = ShuuenUi.Muted, modifier = Modifier.size(24.dp))
      }
    }
    Text(
      label,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun AccuracyRangeBar() {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
      listOf("80%", "70%", "60%", "50%").forEachIndexed { index, label ->
        Text(
          text = label,
          color = if (index == 0) ShuuenUi.Mint else ShuuenUi.Lavender,
          style = MaterialTheme.typography.titleSmall,
        )
      }
    }
    Row(modifier = Modifier.fillMaxWidth().height(18.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
      listOf(
        ShuuenUi.Mint,
        Color(0xFFD7C4FF),
        Color(0xFFA875FF),
        Color(0xFF7C53B8),
      ).forEach { color ->
        Box(
          modifier = Modifier.weight(1f).fillMaxHeight().background(color, RoundedCornerShape(3.dp)),
        )
      }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
      listOf("1-5", "6-10", "11-15", "16-20").forEach {
        Text(it, color = ShuuenUi.Muted, style = MaterialTheme.typography.titleSmall)
      }
    }
  }
}

@Composable
private fun LevelParameters(data: LevelCompleteData) {
  GlassPanel(borderColor = ShuuenUi.BorderStrong) {
    PanelHeading("LEVEL PARAMETERS")
    Text(data.parameterDescription, color = ShuuenUi.Muted, style = MaterialTheme.typography.bodyLarge)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 420.dp
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        data.parameters.chunked(if (compact) 2 else 3).forEach { row ->
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { (icon, text) ->
              ParameterChip(icon, text, Modifier.weight(1f))
            }
            repeat((if (compact) 2 else 3) - row.size) {
              Box(Modifier.weight(1f))
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ParameterChip(
  icon: ImageVector,
  text: String,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier.height(44.dp)) {
    Icon(icon, contentDescription = null, tint = ShuuenUi.Mint, modifier = Modifier.size(22.dp))
    Text(
      text = text,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.titleSmall,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun CompletionActions(
  onRetryLevel: () -> Unit,
  onNextLevel: () -> Unit,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    CompactCompletionButton(
      text = "RETRY LEVEL",
      icon = Icons.Rounded.PlayArrow,
      onClick = onRetryLevel,
      filled = true,
    )
    CompactCompletionButton(
      text = "NEXT LEVEL 3",
      icon = Icons.Rounded.ChevronRight,
      onClick = onNextLevel,
      filled = false,
    )
  }
}

@Composable
private fun PanelHeading(text: String) {
  Text(
    text = text,
    color = ShuuenUi.Mint,
    style = MaterialTheme.typography.titleSmall.copy(
      letterSpacing = 4.sp,
      fontWeight = FontWeight.Bold,
    ),
  )
}

@Composable
private fun CompactCompletionButton(
  text: String,
  icon: ImageVector,
  onClick: () -> Unit,
  filled: Boolean,
) {
  val shape = RoundedCornerShape(14.dp)
  Row(
    modifier = Modifier
      .fillMaxWidth(0.74f)
      .widthIn(max = 360.dp)
      .height(50.dp)
      .clip(shape)
      .background(if (filled) ShuuenUi.Mint else Color.Transparent)
      .border(1.dp, if (filled) Color.Transparent else ShuuenUi.Lavender, shape)
      .clickable(onClick = onClick)
      .padding(horizontal = 18.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
  ) {
    Icon(
      icon,
      contentDescription = null,
      tint = if (filled) Color.Black else ShuuenUi.Lavender,
      modifier = Modifier.size(if (filled) 24.dp else 28.dp),
    )
    Text(
      text = text,
      color = if (filled) Color.Black else ShuuenUi.Lavender,
      style = MaterialTheme.typography.titleMedium.copy(
        letterSpacing = 3.sp,
        fontWeight = FontWeight.Bold,
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

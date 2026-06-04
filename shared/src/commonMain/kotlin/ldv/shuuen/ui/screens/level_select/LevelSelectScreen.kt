package ldv.shuuen.ui.screens.level_select

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.LinearTrainingProgress
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.screens.training.common.TrainingFlow

private data class LevelSelectData(
  val flowTitle: String,
  val key: String,
  val description: String,
  val completedText: String,
  val levels: List<TrainingLevel>,
)

private data class TrainingLevel(
  val number: Int,
  val title: String,
  val subtitle: String,
  val notes: String,
  val questions: String = "20 questions",
  val tempo: String = "96 BPM",
  val range: String = "C3-C5",
  val status: String,
  val percent: String,
  val filledStars: Int,
  val selected: Boolean = false,
  val locked: Boolean = false,
)

@Composable
fun LevelSelectScreen(
  flow: TrainingFlow,
  onNavigateBack: () -> Unit,
  onStartLevel: () -> Unit,
) {
  val data = levelSelectData(flow)
  val selectedLevel = data.levels.firstOrNull { it.selected } ?: data.levels.first()

  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "LEVEL SELECT",
        subtitle = "Choose a training level.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled,
      )
    },
  ) {
    FlowProgressHeader(data)
    data.levels.forEach { level ->
      LevelCard(level)
    }
    PrimaryCta(
      text = "START LEVEL ${selectedLevel.number}",
      onClick = onStartLevel,
      modifier = Modifier.padding(top = 8.dp),
    )
    Text(
      text = "You can change levels anytime in settings.",
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
      textAlign = TextAlign.Center,
    )
  }
}

private fun levelSelectData(flow: TrainingFlow): LevelSelectData {
  return when (flow) {
    TrainingFlow.Singles -> LevelSelectData(
      flowTitle = "SINGLES",
      key = "D Major",
      description = "Identify single notes / degrees.",
      completedText = "13 / 20 levels completed",
      levels = listOf(
        TrainingLevel(
          1,
          "DIATONIC STEPS",
          "Adjacent notes within the scale.",
          "4 notes",
          status = "COMPLETED",
          percent = "100%",
          filledStars = 3
        ),
        TrainingLevel(
          2,
          "TRIAD TONES",
          "Root, third, and fifth of the triad.",
          "3 notes",
          status = "IN PROGRESS",
          percent = "68%",
          filledStars = 2,
          selected = true
        ),
        TrainingLevel(
          3,
          "SCALE DEGREES",
          "All diatonic notes in the scale.",
          "7 notes",
          status = "UNLOCKED",
          percent = "0%",
          filledStars = 0
        ),
        TrainingLevel(
          4,
          "LEAPS",
          "Perfect, major, and minor leaps.",
          "7 notes",
          status = "LOCKED",
          percent = "",
          filledStars = 0,
          locked = true
        ),
        TrainingLevel(
          5,
          "MIXED",
          "Steps, leaps, and triad tones.",
          "7+ notes",
          status = "LOCKED",
          percent = "",
          filledStars = 0,
          locked = true
        ),
      ),
    )

    TrainingFlow.Melodies -> LevelSelectData(
      flowTitle = "MELODIES",
      key = "D Major",
      description = "Transcribe short melody sequences.",
      completedText = "8 / 20 levels completed",
      levels = listOf(
        TrainingLevel(
          1,
          "THREE NOTE ECHOES",
          "Repeat short scale fragments.",
          "3 notes",
          status = "COMPLETED",
          percent = "100%",
          filledStars = 3
        ),
        TrainingLevel(
          2,
          "TRIAD MOTIFS",
          "Root, third, and fifth melody cells.",
          "4 notes",
          status = "IN PROGRESS",
          percent = "68%",
          filledStars = 2,
          selected = true
        ),
        TrainingLevel(
          3,
          "SCALE RUNS",
          "Stepwise melodies in the scale.",
          "5 notes",
          status = "UNLOCKED",
          percent = "0%",
          filledStars = 0
        ),
        TrainingLevel(
          4,
          "LEAP PATTERNS",
          "Mixed step and leap movement.",
          "6 notes",
          status = "LOCKED",
          percent = "",
          filledStars = 0,
          locked = true
        ),
        TrainingLevel(
          5,
          "MIXED MELODIES",
          "Longer mixed melodic phrases.",
          "7+ notes",
          status = "LOCKED",
          percent = "",
          filledStars = 0,
          locked = true
        ),
      ),
    )
  }
}

@Composable
private fun FlowProgressHeader(data: LevelSelectData) {
  GlassPanel(borderColor = ShuuenUi.BorderStrong) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 430.dp
      if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          FlowTitleBlock(data)
          FlowProgressBlock(data)
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
          IconBubble(
            Icons.Rounded.MusicNote, tint = ShuuenUi.Mint, size = 74.dp
          )
          FlowTitleBlock(data, Modifier.weight(1f))
          FlowProgressBlock(data, Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun FlowTitleBlock(data: LevelSelectData, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = data.flowTitle,
      color = ShuuenUi.Mint,
      style = MaterialTheme.typography.titleLarge.copy(
        letterSpacing = 6.sp, fontWeight = FontWeight.Bold
      ),
    )
    Text(data.key, color = ShuuenUi.Text, style = MaterialTheme.typography.headlineLarge)
    Text(data.description, color = ShuuenUi.Muted, style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
private fun FlowProgressBlock(data: LevelSelectData, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(
      text = "PROGRESS",
      color = ShuuenUi.Lavender,
      style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 3.sp),
    )
    Text("68%", color = ShuuenUi.Lavender, style = MaterialTheme.typography.displayMedium)
    LinearTrainingProgress(
      progress = 0.68f, color = ShuuenUi.Mint
    )
    Text(data.completedText, color = ShuuenUi.Muted, style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
private fun LevelCard(level: TrainingLevel) {
  val lockedAlpha = if (level.locked) 0.48f else 1f

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = ShuuenUi.Panel,
    contentColor = ShuuenUi.Text,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, if (level.selected) ShuuenUi.Lavender else ShuuenUi.Border),
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      val compact = maxWidth < 430.dp
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        LevelNumberBadge(level.number, level.selected, level.locked)
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          if (compact) {
            LevelText(level, lockedAlpha)
            LevelStatus(level, lockedAlpha)
          } else {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
              LevelText(level, lockedAlpha, Modifier.weight(1f))
              LevelStatus(level, lockedAlpha)
            }
          }
          LevelParameterGrid(level = level, compact = compact)
        }
      }
    }
  }
}

@Composable
private fun LevelText(level: TrainingLevel, alpha: Float, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = "LEVEL ${level.number}",
      color = if (level.selected) ShuuenUi.Lavender else ShuuenUi.Mint.copy(alpha = alpha),
      style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 3.sp),
    )
    Text(
      text = level.title,
      color = ShuuenUi.Text.copy(alpha = alpha),
      style = MaterialTheme.typography.titleLarge.copy(
        letterSpacing = 3.sp, fontWeight = FontWeight.Bold
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    Text(
      text = level.subtitle,
      color = ShuuenUi.Muted.copy(alpha = alpha),
      style = MaterialTheme.typography.bodyLarge,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun LevelStatus(level: TrainingLevel, alpha: Float) {
  Column(
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Text(
      text = level.status,
      color = if (level.selected) ShuuenUi.Lavender else ShuuenUi.Mint.copy(alpha = alpha),
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.7.sp),
      maxLines = 1,
    )
    if (level.locked) {
      Icon(
        Icons.Rounded.Lock,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(30.dp)
      )
    } else {
      Text(
        text = level.percent,
        color = if (level.selected) ShuuenUi.Lavender else ShuuenUi.Mint.copy(alpha = alpha),
        style = MaterialTheme.typography.headlineLarge,
      )
      StarRating(
        filled = level.filledStars,
        total = 3,
        tint = if (level.selected) ShuuenUi.Lavender else ShuuenUi.Mint,
        alpha = alpha
      )
    }
  }
}

@Composable
private fun LevelNumberBadge(number: Int, selected: Boolean, locked: Boolean) {
  Box(modifier = Modifier.size(68.dp), contentAlignment = Alignment.Center) {
    Canvas(Modifier.fillMaxWidth().height(68.dp)) {
      val stroke = 1.4.dp.toPx()
      val color = when {
        selected -> ShuuenUi.Lavender
        locked -> ShuuenUi.Muted.copy(alpha = 0.55f)
        else -> ShuuenUi.Mint
      }
      drawCircle(
        color = color.copy(alpha = 0.75f),
        radius = size.minDimension / 2f - stroke,
        style = Stroke(stroke)
      )
      drawCircle(
        color = color.copy(alpha = 0.32f),
        radius = size.minDimension / 2f - 8.dp.toPx(),
        style = Stroke(stroke)
      )
    }
    Text(
      text = number.toString(),
      color = when {
        selected -> ShuuenUi.Lavender
        locked -> ShuuenUi.Muted
        else -> ShuuenUi.Mint
      },
      style = MaterialTheme.typography.displayMedium,
    )
  }
}

@Composable
private fun StarRating(
  filled: Int,
  total: Int,
  tint: Color,
  alpha: Float = 1f,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
    repeat(total) { index ->
      Text(
        text = if (index < filled) "★" else "☆",
        color = tint.copy(alpha = if (index < filled) alpha else alpha * 0.7f),
        style = MaterialTheme.typography.titleLarge,
      )
    }
  }
}

@Composable
private fun LevelParameterGrid(
  level: TrainingLevel,
  compact: Boolean,
) {
  val items = listOf(
    Triple(level.notes, Icons.Rounded.MusicNote, 1f),
    Triple(level.questions, Icons.AutoMirrored.Rounded.HelpOutline, 1.25f),
    Triple(level.tempo, Icons.Rounded.Speed, 1f),
    Triple(level.range, Icons.Rounded.Keyboard, 1f),
  )

  if (compact) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items.chunked(2).forEach { row ->
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          row.forEach { (text, icon, _) ->
            LevelParameter(text, icon, level.selected, Modifier.weight(1f))
          }
        }
      }
    }
  } else {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      items.forEach { (text, icon, weight) ->
        LevelParameter(text, icon, level.selected, Modifier.weight(weight))
      }
    }
  }
}

@Composable
private fun LevelParameter(
  text: String,
  icon: ImageVector,
  selected: Boolean,
  modifier: Modifier = Modifier,
) {
  SoftControl(
    modifier = modifier.height(42.dp), selected = false
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = if (selected) ShuuenUi.Lavender else ShuuenUi.Muted,
      modifier = Modifier.size(18.dp),
    )
    Text(
      text = text,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyLarge,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

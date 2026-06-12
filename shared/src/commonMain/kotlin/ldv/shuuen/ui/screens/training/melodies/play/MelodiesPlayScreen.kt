package ldv.shuuen.ui.screens.training.melodies.play

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ldv.shuuen.ui.common.LinearTrainingProgress
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.inputs.FifthsCircle
import ldv.shuuen.ui.common.music.inputs.PianoKeyboard
import ldv.shuuen.ui.common.music.inputs.PianoKeyboardDefaults

@Composable
fun MelodiesPlayScreen(onNavigateBack: () -> Unit, onLevelEnd: () -> Unit) {
  var useCircleInput by rememberSaveable { mutableStateOf(false) }

  StaticScreenFrame(
    scrollable = false,
    topBar = {
      ShuuenTopAppBar(
        title = "D Major",
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Tune,
        onTrailingClick = { useCircleInput = !useCircleInput },
        type = ShuuenTopAppBarType.Simple,
      )
    },
  ) {

    MelodiesTrainingStatus()

    Spacer(Modifier.weight(1f))

    MelodyInputCells(useCircleInput = useCircleInput)

    if (useCircleInput) {
      CircleAnswerArea()
    } else {
      Spacer(Modifier.height(14.dp))
      KeyboardAnswerArea()
    }

    Spacer(Modifier.weight(0.34f))

    BottomActionBar(on1 = onLevelEnd)
  }
}

@Composable
private fun MelodiesTrainingStatus() {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top,
    ) {
      Text(
        "1/30",
        color = ShuuenUi.Muted,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.weight(1f),
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        ScoreCount("0", ShuuenUi.Correct)
        Text("|", color = ShuuenUi.Dim, style = MaterialTheme.typography.titleMedium)
        ScoreCount("0", ShuuenUi.Incorrect)
      }
    }

    LinearTrainingProgress(progress = 0.045f)
  }
}

@Composable
private fun ScoreCount(
  value: String,
  tint: Color,
) {
  Text(value, color = tint, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun MelodyInputCells(useCircleInput: Boolean) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val values = if (useCircleInput) listOf("1", "3", "5") else listOf("C4", "E4", "G4")

    values.forEach {
      MelodyInputCell(text = it, selected = !useCircleInput, modifier = Modifier.weight(1f))
    }

    repeat(3) {
      MelodyInputCell(text = "-", empty = true, modifier = Modifier.weight(1f))
    }
  }
}

@Composable
private fun MelodyInputCell(
  text: String,
  modifier: Modifier = Modifier,
  selected: Boolean = false,
  empty: Boolean = false,
) {
  val shape = ShuuenUi.ControlShape
  Box(
    modifier = modifier.height(52.dp).clip(shape)
      .background(if (empty) Color.Transparent else Color.White.copy(alpha = 0.05f))
      .border(
        width = 1.dp,
        color = when {
          selected -> ShuuenUi.HairlineStrong
          else -> ShuuenUi.Hairline
        },
        shape = shape,
      ),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      color = if (empty) ShuuenUi.Dim else ShuuenUi.Text,
      style = MaterialTheme.typography.titleLarge,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun KeyboardAnswerArea() {
  PianoKeyboard(
    modifier = Modifier.fillMaxWidth().aspectRatio(PianoKeyboardDefaults.aspectRatio(12)),
    keyCount = 12,
  )
}

@Composable
private fun BottomActionBar(on1: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BottomRepeatButton(Modifier.weight(1.8f).clickable { on1() })
    BottomIconButton(
      icon = Icons.Rounded.MusicNote,
      modifier = Modifier.width(72.dp),
    )
    BottomIconButton(
      icon = Icons.AutoMirrored.Rounded.Backspace,
      modifier = Modifier.width(72.dp),
    )
    BottomIconButton(
      icon = Icons.Rounded.Flag,
      modifier = Modifier.width(64.dp),
    )
  }
}

@Composable
private fun BottomRepeatButton(modifier: Modifier = Modifier) {
  SoftControl(modifier = modifier.height(60.dp)) {
    Icon(
      imageVector = Icons.Rounded.Replay,
      contentDescription = null,
      tint = ShuuenUi.Text,
      modifier = Modifier.size(24.dp),
    )
    Text(
      text = "Repeat",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
      textAlign = TextAlign.Center,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun BottomIconButton(
  icon: ImageVector,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier.height(60.dp)) {
    Icon(icon, contentDescription = null, tint = ShuuenUi.Muted, modifier = Modifier.size(24.dp))
  }
}

@Composable
private fun CircleAnswerArea() {
  FifthsCircle(
    modifier = Modifier.fillMaxWidth(),
    dotEdgePadding = 0.dp,
    centerButtonSize = 64.dp,
    centerContent = {
      Icon(
        imageVector = Icons.Rounded.FastForward,
        contentDescription = null,
        tint = ShuuenUi.Text,
        modifier = Modifier.size(72.dp),
      )
    },
  )
}

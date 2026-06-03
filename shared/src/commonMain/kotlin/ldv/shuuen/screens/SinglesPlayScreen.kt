package ldv.shuuen.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ldv.shuuen.ui.music.FifthsCircle
import ldv.shuuen.ui.music.PianoKeyboard
import ldv.shuuen.ui.music.PianoKeyboardDefaults

@Composable
fun SinglesPlayScreen(onNavigateBack: () -> Unit) {
  var useCircleInput by rememberSaveable { mutableStateOf(false) }

  StaticScreenFrame(
    scrollable = false,
    topBar = {
      ShuuenTopAppBar(
        title = "D Major",
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Tune,
        onTrailingClick = { useCircleInput = !useCircleInput },
      )
    },
  ) {

    TrainingStatus()

    Spacer(Modifier.weight(1f))

    if (useCircleInput) {
      CircleAnswerArea()
    } else {
      KeyboardAnswerArea()
    }

    Spacer(Modifier.weight(0.34f))

    BottomActionBar()
  }
}

@Composable
private fun TrainingStatus() {
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        ScorePill("0", Icons.Outlined.CheckCircle, ShuuenUi.Green)
        Text("|", style = MaterialTheme.typography.titleMedium)
        ScorePill("0", Icons.Outlined.Cancel, ShuuenUi.Red)
      }
    }

    LinearTrainingProgress(
      progress = 0.045f,
      color = ShuuenUi.Lavender,
    )
  }
}

@Composable
private fun ScorePill(
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Text(value, color = tint, style = MaterialTheme.typography.titleLarge)
//    Icon(icon, contentDescription = null, tint = tint)
  }
}

@Composable
private fun KeyboardAnswerArea() {
  PianoKeyboard(
    modifier = Modifier.fillMaxWidth().aspectRatio(PianoKeyboardDefaults.aspectRatio(12)),
    keyCount = 12,
    pressedKeyColors = PianoKeyboardDefaults.pressedColors(12, ShuuenUi.Lavender),
  )
}

@Composable
private fun BottomActionBar() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BottomRepeatButton(Modifier.weight(1.8f))
    BottomIconButton(
      icon = Icons.Rounded.MusicNote,
      tint = ShuuenUi.Lavender,
      modifier = Modifier.width(80.dp),
    )
    Spacer(Modifier.weight(0.34f))
    BottomIconButton(
      icon = Icons.Rounded.Flag,
      tint = ShuuenUi.Muted,
      modifier = Modifier.width(64.dp),
    )
  }
}

@Composable
private fun BottomRepeatButton(modifier: Modifier = Modifier) {
  SoftControl(modifier = modifier.height(68.dp)) {
    Icon(
      imageVector = Icons.Rounded.Replay,
      contentDescription = null,
      tint = ShuuenUi.Mint,
      modifier = Modifier.size(26.dp),
    )
    Text(
      text = "Repeat",
      color = ShuuenUi.Mint,
      style = MaterialTheme.typography.titleSmall,
      textAlign = TextAlign.Center,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun BottomIconButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier.height(68.dp)) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
  }
}

@Composable
private fun CircleAnswerArea() {
  FifthsCircle(
    modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, Color.Red)),
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

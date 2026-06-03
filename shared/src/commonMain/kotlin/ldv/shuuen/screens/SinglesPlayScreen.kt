package ldv.shuuen.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.SkipNext
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
    topBar = {
      ShuuenTopAppBar(
        title = "SINGLES",
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Tune,
      )
    },
  ) {

    TrainingStatus()

    PillControl(
      text = if (useCircleInput) "Circle input" else "Keyboard input",
      selected = false,
      onClick = { useCircleInput = !useCircleInput },
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      ActionButton("Prompt", Icons.Rounded.Replay, ShuuenUi.Mint, Modifier.weight(1f))
      ActionButton("Note", Icons.Rounded.MusicNote, ShuuenUi.Lavender, Modifier.weight(1f))
      ActionButton("Skip", Icons.Rounded.SkipNext, ShuuenUi.Muted, Modifier.weight(1f))
    }

    if (useCircleInput) {
      CircleAnswerArea()
    } else {
      KeyboardAnswerArea()
    }
  }
}

@Composable
private fun TrainingStatus() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text("D Major", color = ShuuenUi.Text, style = MaterialTheme.typography.titleMedium)
      Text("1/30", color = ShuuenUi.Muted, style = MaterialTheme.typography.titleSmall)
    }

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
private fun ActionButton(
  text: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier.height(68.dp)) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
      Text(
        text = text,
        color = tint,
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
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
package ldv.shuuen.ui.screens.training.single.play

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakira.napier.Napier
import ldv.shuuen.common.ResponseState
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.ui.common.LinearTrainingProgress
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.inputs.PianoKeyboard
import ldv.shuuen.ui.common.music.inputs.PianoKeyboardDefaults

@Composable
fun SinglesPlayScreen(
  onNavigateBack: () -> Unit, onLevelEnd: () -> Unit, viewModel: SinglesPlayScreenViewModel
) {
  val screenState by viewModel.state.collectAsStateWithLifecycle()
  var useCircleInput by rememberSaveable { mutableStateOf(false) }
  val title = when (val level = screenState.levelData) {
    is ResponseState.Loading -> "Loading..."
    is ResponseState.Error -> "Error"
    is ResponseState.Success -> level.result.name
  }

  LaunchedEffect(screenState.phase) {
    when (screenState.phase) {
      is QuizPhase.Complete -> onLevelEnd()
      // for now
      else -> Unit
    }
  }

  StaticScreenFrame(
    scrollable = false,
    topBar = {
      ShuuenTopAppBar(
        title = title,
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Tune,
        onTrailingClick = { useCircleInput = !useCircleInput },
        type = ShuuenTopAppBarType.Simple,
      )
    },
  ) {

    screenState.quizState?.let {
      TrainingStatus(
        it.currentQuestionNumber, it.correctAnswers, it.incorrectAnswers.size, it.questionsNumber
      )
    }

    Spacer(Modifier.weight(1f))

//    if (useCircleInput) {
//      FifthsCircle(
//        modifier = Modifier.fillMaxWidth(),
////      .border(BorderStroke(1.dp, Color.Red)),
//        dotEdgePadding = 0.dp,
//        centerButtonSize = 64.dp,
//      )
//    } else {
    val indication by viewModel.answerIndications.collectAsStateWithLifecycle()
//    val keyColors by viewModel.keyColors.collectAsStateWithLifecycle()
//    val keyColors = screenState.quizState?.root?.let {
//      PianoKeyboardDefaults.colorfulPressedColors(12, it)
//    } ?: PianoKeyboardDefaults.pressedColors(12)
    val keyColors: List<Color> = when (screenState.phase) {
      is QuizPhase.LoadingContext -> {
        val root = screenState.quizState?.root
        if (root == null) PianoKeyboardDefaults.pressedColors(12)
        else {
          PianoKeyboardDefaults.colorfulPressedColors(
            12, root
          )
        }
      }

      else -> {
        val currentNote = screenState.quizState?.currentNote
        if (currentNote == null) PianoKeyboardDefaults.pressedColors(12)
        else {
          Pitch.entries.map {
            if (it.ordinal == currentNote.pitch.ordinal) AnswerColors.Correct.color else AnswerColors.Incorrect.color
          }
        }
      }
    }
    Napier.v { "screen state phase: ${screenState.phase}" }
    PianoKeyboard(
      modifier = Modifier.fillMaxWidth().aspectRatio(PianoKeyboardDefaults.aspectRatio(12)),
      keyCount = 12,
      pressedKeyColors = keyColors,
      programmaticIndications = indication,
      onKeyPressedChange = { offset, pressed ->
        if (!pressed) {
          val pitch = Pitch.fromOrdinal(offset)
          viewModel.userGuessed(pitch)
        }
      })
//    }

    Spacer(Modifier.weight(0.34f))

    BottomActionBar(on1 = { viewModel.repeatNote() })
  }
}

@Composable
private fun TrainingStatus(
  questionNumber: Int = 1, correct: Int = 0, incorrect: Int = 0, questionsAmount: Int? = null
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.Top,
    ) {
      Text(
        "$questionNumber/${questionsAmount ?: "∞"}",
        color = ShuuenUi.Muted,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.weight(1f),
      )

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
      ) {
        ScorePill("$correct", Icons.Outlined.CheckCircle, ShuuenUi.Green)
        Text("|", style = MaterialTheme.typography.titleMedium)
        ScorePill("$incorrect", Icons.Outlined.Cancel, ShuuenUi.Red)
      }
    }

    LinearTrainingProgress(
      progress = (questionNumber.toFloat() - 1) / (questionsAmount ?: questionNumber),
      color = ShuuenUi.Lavender,
    )
  }
}

@Composable
private fun ScorePill(
  value: String,
  icon: ImageVector,
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
private fun BottomActionBar(on1: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BottomRepeatButton(Modifier.weight(1.8f).clickable { on1() })
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
  icon: ImageVector,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier.height(68.dp)) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
  }
}

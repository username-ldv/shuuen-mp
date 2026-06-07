package ldv.shuuen.ui.screens.free_play

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.FifthsCircle
import ldv.shuuen.ui.common.music.FifthsCircleIndication
import ldv.shuuen.ui.common.music.PianoKeyIndication
import ldv.shuuen.ui.common.music.PianoKeyboard
import ldv.shuuen.ui.common.music.PianoKeyboardDefaults

@Composable
fun FreePlayScreen(
  viewModel: FreePlayViewModel,
  onNavigateBack: () -> Unit,
) {
  val state by viewModel.state.collectAsState()

  DisposableEffect(viewModel) {
    onDispose {
      viewModel.onAction(FreePlayAction.StopAll)
    }
  }

  StaticScreenFrame(
    maxWidth = 720.dp,
    topBar = {
      ShuuenTopAppBar(
        title = "FREE PLAY",
        onBack = onNavigateBack,
      )
    },
  ) {
    Text(
      text = state.tonic.toString(),
      style = MaterialTheme.typography.headlineLarge,
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )

    if (state.initializingAudio) {
      Text(
        text = "Preparing audio...",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.align(Alignment.CenterHorizontally),
      )
    }

    state.errorMessage?.let { message ->
      Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.align(Alignment.CenterHorizontally),
      )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val circleWidth = maxWidth.coerceAtMost(460.dp)
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        FifthsCircle(
          modifier = Modifier
            .width(circleWidth)
            .aspectRatio(1f),
          itemNames = circleLabels(state),
          onItemPressedChange = { index, pressed ->
            if (pressed) viewModel.onAction(FreePlayAction.ToggleDrone(index))
          },
          programmaticIndications = state.activeFifthsItems.map {
            FifthsCircleIndication(it, durationMillis = null)
          },
          onCenterClick = { viewModel.onAction(FreePlayAction.StopAll) },
        )
      }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val keyboardWidth = maxWidth.coerceAtMost(720.dp)
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        PianoKeyboard(
          modifier = Modifier
            .width(keyboardWidth)
            .aspectRatio(PianoKeyboardDefaults.aspectRatio(12)),
          enabledKeys = state.enabledKeyboardKeys,
          onKeyPressedChange = { index, pressed ->
            viewModel.onAction(
              if (pressed) {
                FreePlayAction.PressPitch(index)
              } else {
                FreePlayAction.ReleasePitch(index)
              },
            )
          },
          programmaticIndications = state.activeKeyboardKeys.map {
            PianoKeyIndication(it, durationMillis = null)
          },
          pressedKeyColors = PianoKeyboardDefaults.colorfulPressedColors(12, state.tonic),
        )
      }
    }
  }
}

private fun circleLabels(state: FreePlayState): List<String> =
  when (state.displayMode) {
    FreePlayDisplayMode.Degrees -> Degree.chromaticOrder.map { it.toString() }
    FreePlayDisplayMode.Notes -> Degree.chromaticOrder.map { it.pitch(state.tonic).toString() }
  }

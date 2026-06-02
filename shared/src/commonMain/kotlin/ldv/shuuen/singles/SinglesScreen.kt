package ldv.shuuen.singles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ldv.shuuen.music.Degree
import ldv.shuuen.ui.music.FifthsCircle
import ldv.shuuen.ui.music.FifthsCircleIndication
import ldv.shuuen.ui.music.PianoKeyIndication
import ldv.shuuen.ui.music.PianoKeyboard
import ldv.shuuen.ui.music.PianoKeyboardDefaults

@Composable
fun SinglesScreen(
    viewModel: SinglesViewModel,
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.onAction(SinglesAction.StopAll)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TextButton(onClick = onNavigateBack) {
                Text("Back")
            }
        }

        item {
            Text(
                text = state.tonic.toString(),
                style = MaterialTheme.typography.headlineLarge,
            )
        }

        if (state.initializingAudio) {
            item {
                Text(
                    text = "Preparing audio...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        state.errorMessage?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        item {
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
                            if (pressed) viewModel.onAction(SinglesAction.ToggleDrone(index))
                        },
                        programmaticIndications = state.activeFifthsItems.map {
                            FifthsCircleIndication(it, durationMillis = null)
                        },
                        onCenterClick = { viewModel.onAction(SinglesAction.StopAll) },
                    )
                }
            }
        }

        item {
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
                                    SinglesAction.PressPitch(index)
                                } else {
                                    SinglesAction.ReleasePitch(index)
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
}

private fun circleLabels(state: SinglesState): List<String> =
    when (state.displayMode) {
        SinglesDisplayMode.Degrees -> Degree.chromaticOrder.map { it.toString() }
        SinglesDisplayMode.Notes -> Degree.chromaticOrder.map { it.pitch(state.tonic).toString() }
    }

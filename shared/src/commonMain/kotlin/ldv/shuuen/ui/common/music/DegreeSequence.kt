package ldv.shuuen.ui.common.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.ui.common.ShuuenUi

/**
 * Building blocks for editing degree sequences (context nodes, setup melodies).
 * Pure UI: callers own the sequence state.
 */

@Composable
fun DegreeChip(
  label: String,
  modifier: Modifier = Modifier,
  inverted: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  Box(
    modifier = modifier
      .height(34.dp)
      .widthIn(min = 38.dp)
      .clip(ShuuenUi.ControlShape)
      .background(if (inverted) ShuuenUi.Inverse else Color.White.copy(alpha = 0.05f))
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = label,
      color = if (inverted) ShuuenUi.OnInverse else ShuuenUi.Muted,
      style = MaterialTheme.typography.titleSmall,
      maxLines = 1,
    )
  }
}

/** All twelve degrees in chromatic order; tapping one appends it to the sequence. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DegreePalette(
  onPick: (Degree) -> Unit,
  modifier: Modifier = Modifier,
) {
  FlowRow(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Degree.chromaticOrder.forEach { degree ->
      DegreeChip(
        label = degree.label,
        onClick = { onPick(degree) },
      )
    }
  }
}

/** The built sequence rendered as inverted chips, with an optional backspace control. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DegreeSequenceChips(
  labels: List<String>,
  modifier: Modifier = Modifier,
  emptyPlaceholder: String = "—",
  onBackspace: (() -> Unit)? = null,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    FlowRow(
      modifier = Modifier.weight(1f),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
      verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      if (labels.isEmpty()) {
        DegreeChip(label = emptyPlaceholder)
      } else {
        labels.forEach { label ->
          DegreeChip(label = label, inverted = true)
        }
      }
    }
    if (onBackspace != null) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(ShuuenUi.ControlShape)
          .background(Color.White.copy(alpha = 0.05f))
          .clickable(onClick = onBackspace),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.Backspace,
          contentDescription = "Remove last degree",
          tint = ShuuenUi.Muted,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

/**
 * Inline editor for a plain degree sequence (e.g. a node's setup melody):
 * current chips on top, palette below to append.
 */
@Composable
fun DegreeSequenceEditor(
  degrees: List<Degree>,
  onAppend: (Degree) -> Unit,
  onBackspace: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    DegreeSequenceChips(
      labels = degrees.map { it.label },
      onBackspace = onBackspace,
    )
    DegreePalette(onPick = onAppend)
  }
}

/** Compact stepper for the first degree's octave. */
@Composable
fun OctaveStepper(
  value: Int,
  onChange: (Int) -> Unit,
  modifier: Modifier = Modifier,
  range: IntRange = 0..8,
) {
  Row(
    modifier = modifier
      .height(34.dp)
      .clip(ShuuenUi.PillShape)
      .background(Color.White.copy(alpha = 0.05f)),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    StepperPiece("−") { if (value > range.first) onChange(value - 1) }
    Text(
      text = "Oct $value",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleSmall,
      textAlign = TextAlign.Center,
      modifier = Modifier.widthIn(min = 48.dp),
      maxLines = 1,
    )
    StepperPiece("+") { if (value < range.last) onChange(value + 1) }
  }
}

@Composable
private fun StepperPiece(text: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .size(34.dp)
      .clip(ShuuenUi.ControlShape)
      .clickable(onClick = onClick),
    contentAlignment = Alignment.Center,
  ) {
    Text(text = text, color = ShuuenUi.Muted, style = MaterialTheme.typography.titleMedium)
  }
}

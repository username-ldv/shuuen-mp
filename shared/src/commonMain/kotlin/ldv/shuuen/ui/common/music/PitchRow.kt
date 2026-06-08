package ldv.shuuen.ui.common.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.ui.theme.ShuuenTheme

data class PitchState(val active: Boolean, val name: String)

@Composable
fun PitchRow(
  pitches: Map<Pitch, PitchState>,
  modifier: Modifier = Modifier,
  onClick: (Pitch) -> Unit = {}
) {
  LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
    pitches.forEach { pitch ->
      item {
        BoxedListItem(
          label = pitch.value.name,
          active = pitch.value.active,
          onClicked = { onClick(pitch.key) }
        )
      }
    }
  }
}

@Preview
@Composable
fun PitchRowPreview() {
  ShuuenTheme {
    PitchRow(pitches = Scale.major(Pitch.C).pitches.map { it to PitchState(true, it.toString()) }
      .toMap())
  }
}
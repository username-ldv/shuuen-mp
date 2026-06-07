package ldv.shuuen.ui.common.music

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.ui.theme.ShuuenTheme

// todo: use Note.pianoKeyNumber
@Composable
fun NoteRow(
  value: Note,
  // lowest, highest
  bounds: Pair<Note, Note> = Note(Pitch.A, 0) to Note(Pitch.C, 8),
  nameGenerator: ((note: Note) -> String)? = null,
  itemSize: Dp = 50.dp,
  onChoose: (note: Note) -> Unit = {},
) {
  val selectedIndex = value.midiIndex - Note.MidiOffset
  BoxWithConstraints {
    val density = LocalDensity.current

    val itemWidthPx = with(density) { itemSize.roundToPx() }
    val viewportWidthPx = with(density) { maxWidth.roundToPx() }
    val centerOffsetPx = -(viewportWidthPx / 2 - itemWidthPx / 2)

    val listState = rememberLazyListState(
      initialFirstVisibleItemIndex = selectedIndex.coerceAtLeast(0),
      initialFirstVisibleItemScrollOffset = centerOffsetPx
    )
    LazyRow(state = listState) {
      for (note in bounds.first..bounds.second) {
        item {
          NoteListItem(
            note = note,
            active = value.midiIndex == note.midiIndex,
            onClicked = { onChoose(note) },
            customName = nameGenerator?.invoke(note),
            itemSize = itemSize
          )
          Spacer(modifier = Modifier.width(4.dp))
        }
      }
    }
  }
}

@Composable
fun NoteListItem(
  note: Note,
  active: Boolean,
  itemSize: Dp,
  onClicked: (note: Note) -> Unit = {},
  customName: String? = null,
) {
  val surfaceColor = if (active) {
    MaterialTheme.colorScheme.surfaceVariant
  } else {
    MaterialTheme.colorScheme.surfaceContainer
  }
  Surface(
    color = surfaceColor,
    modifier = Modifier.size(itemSize),
    shape = RoundedCornerShape(4.dp),
    shadowElevation = 4.dp,
    onClick = {
      onClicked(note)
    }) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = customName ?: note.name,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp
      )
    }
  }
}

@Preview
@Composable
fun NoteRowPreview() {
  ShuuenTheme {
    NoteRow(Note(Pitch.C))
  }
}
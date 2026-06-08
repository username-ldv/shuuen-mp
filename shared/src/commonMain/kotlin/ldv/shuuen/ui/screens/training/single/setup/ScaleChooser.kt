package ldv.shuuen.ui.screens.training.single.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.PillControl
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.music.PitchRow
import ldv.shuuen.ui.theme.ShuuenTheme

@Composable
fun ScaleChooser(trainingScale: TrainingScale, onScaleChosen: (TrainingScale) -> Unit = {}) {
  var tonic by rememberSaveable { mutableStateOf(Pitch.C) }
  var mode by rememberSaveable { mutableStateOf(ScaleType.Major) }
  LaunchedEffect(tonic, mode) {
    val scale = when (mode) {
      ScaleType.Major -> Scale.major(tonic)
      ScaleType.NaturalMinor -> Scale.naturalMinor(tonic)
      ScaleType.Custom -> Scale.custom(tonic, listOf(0))
    }
    onScaleChosen(TrainingScale.fromScale(scale))
  }
  GlassPanel {
    val arrangementSpace = 14.dp
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(arrangementSpace)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
      ) {
        IconBubble(
          Icons.Rounded.MusicNote, tint = ShuuenUi.Mint, size = 64.dp
        )
        Column(
          modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "1. SCALE",
            color = ShuuenUi.Text,
            style = MaterialTheme.typography.titleLarge.copy(
              letterSpacing = 2.4.sp, fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
          Text(
            "Choose the scale you want to train.",
            color = ShuuenUi.Muted,
            style = MaterialTheme.typography.bodyMedium
          )
        }
        Icon(
          Icons.Rounded.ExpandLess,
          contentDescription = null,
          tint = ShuuenUi.Muted,
          modifier = Modifier.size(30.dp)
        )
      }
      Column(
        verticalArrangement = Arrangement.spacedBy(arrangementSpace)
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()
        ) {
          DropdownMenu(
            items = Pitch.entries.map { Scale.appropriatePitchName(it, it, mode) },
            selectedItem = Scale.appropriatePitchName(tonic, tonic, mode),
            onItemSelected = {
              tonic = Pitch.fromName(it) ?: error("no pitch")
            },
            modifier = Modifier.weight(0.75f)
          )
          DropdownMenu(
            items = ScaleType.entries.map { it.toString() },
            selectedItem = mode.toString(),
            onItemSelected = {
              mode = ScaleType.fromName(it) ?: error("invalid scale")
            },
            modifier = Modifier.weight(1f)
          )
        }
        PitchRow(
          pitches = trainingScale.pitchStates,
          onClick = { pitch ->
            val m = trainingScale.pitchStates.toMutableMap()
            m[pitch]?.let { m[pitch] = it.copy(active = !it.active) }
            onScaleChosen(TrainingScale(m))
          })
        PillControl(
          "More scales", leadingIcon = Icons.Rounded.Casino, modifier = Modifier.fillMaxWidth()
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenu(
  items: List<String>,
  selectedItem: String,
  onItemSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null,
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    modifier = modifier,
  ) {
    OutlinedTextField(
      value = selectedItem,
      onValueChange = {},
      readOnly = true,
      singleLine = true,
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
      },
      modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        .fillMaxWidth(),
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = {
        expanded = false
        focusManager.clearFocus()
      },
      modifier = Modifier.exposedDropdownSize(matchAnchorWidth = true),
    ) {
      items.forEach { item ->
        DropdownMenuItem(
          text = { Text(item) },
          onClick = {
            onItemSelected(item)
            expanded = false
            focusManager.clearFocus()
          },
          trailingIcon = {
            if (item == selectedItem) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
              )
            }
          },
        )
      }
    }
  }
}

@Preview
@Composable
private fun DropdownMenuPreview() {
  ShuuenTheme {
    DropdownMenu(
      items = Pitch.entries.map { it.toString() },
      selectedItem = Pitch.C.toString(),
      onItemSelected = {},
    )
  }
}
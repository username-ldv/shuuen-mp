package ldv.shuuen.ui.common.music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.MusicNote
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.domain.training.TrainingScale
import ldv.shuuen.domain.training.TrainingScaleItemStates
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.TextDropdownMenu

@Composable
fun ScaleChooser(trainingScale: TrainingScale, onScaleChosen: (TrainingScale) -> Unit = {}) {
  GlassPanel {
    val arrangementSpace = 14.dp
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.spacedBy(arrangementSpace)
    ) {
      Header()
      var tonic: Pitch? by rememberSaveable { mutableStateOf(null) }
      var mode by rememberSaveable { mutableStateOf(ScaleType.Major) }
      LaunchedEffect(tonic, mode) {
        val newTrainingScale = tonic?.let { t ->
          TrainingScale.fromScale(Scale.fromScaleType(t, mode, listOf(0)))
        } ?: TrainingScale.degreesFromType(mode, listOf(0))
        onScaleChosen(newTrainingScale)
      }
      Column(
        verticalArrangement = Arrangement.spacedBy(arrangementSpace),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()
        ) {
          TextDropdownMenu(
            items = listOf("Random") + Pitch.entries.map {
            Scale.appropriatePitchName(
              it, it, mode
            )
          },
            selectedItem = tonic?.let { Scale.appropriatePitchName(it, it, mode) } ?: "Random",
            onItemSelected = { tonic = Pitch.fromName(it) },
            modifier = Modifier.weight(0.75f)
          )
          TextDropdownMenu(
            items = ScaleType.entries.map { it.toString() },
            selectedItem = mode.toString(),
            onItemSelected = {
              mode = ScaleType.fromName(it) ?: error("invalid scale")
            },
            modifier = Modifier.weight(1f)
          )
        }
        when (trainingScale.itemStates) {
          is TrainingScaleItemStates.ByPitch -> {
            BoxedItemRow(
              items = trainingScale.itemStates.items, onClick = { pitch ->
                val m = trainingScale.itemStates.items.toMutableMap()
                m[pitch]?.let { m[pitch] = it.copy(active = !it.active) }
                onScaleChosen(TrainingScale(tonic, TrainingScaleItemStates.ByPitch(m)))
              })
          }

          is TrainingScaleItemStates.ByDegree -> {
            BoxedItemRow(
              items = trainingScale.itemStates.items, onClick = { degree ->
                val m = trainingScale.itemStates.items.toMutableMap()
                m[degree]?.let { m[degree] = it.copy(active = !it.active) }
                onScaleChosen(TrainingScale(tonic, TrainingScaleItemStates.ByDegree(m)))
              })
          }
        }
      }
    }
  }
}

@Composable
private fun Header() {
  Row(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    IconBubble(
      Icons.Rounded.MusicNote, tint = ShuuenUi.Mint, size = 64.dp
    )
    Column(
      modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(
        text = "1. SCALE",
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
}
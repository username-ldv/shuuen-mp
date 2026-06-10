package ldv.shuuen.ui.screens.training.single.level_select

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ldv.shuuen.common.ResponseState
import ldv.shuuen.domain.training.TrainingScaleItemStates
import ldv.shuuen.domain.training.singles.SinglesLevel
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.BoxedItemRow

@Composable
fun SinglesLevelSelectScreen(
  onNavigateBack: () -> Unit,
  onStartLevel: (levelId: String) -> Unit,
  onCreateNewLevel: () -> Unit,
  viewModel: SinglesLevelSelectScreenViewModel
) {
  val levels by viewModel.levels.collectAsStateWithLifecycle(ResponseState.Loading)
  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "LEVEL SELECT",
        subtitle = "Choose a training level.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled,
      )
    }, scrollable = false
  ) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
      item {
        PrimaryCta(
          text = "CREATE NEW",
          icon = Icons.Rounded.Create,
          onClick = onCreateNewLevel,
          modifier = Modifier.padding(top = 8.dp),
        )
      }
      when (val l = levels) {
        is ResponseState.Loading -> item { Text(text = "Loading...") }
        is ResponseState.Success -> l.result.forEach { level ->
          item {
            LevelCard(level, onLevelChosen = { onStartLevel(it.id) })
          }
        }

        is ResponseState.Error -> item { Text(text = "Error loading levels: ${l.throwable.message}") }
      }
    }
  }
}

@Composable
private fun LevelCard(level: SinglesLevel, onLevelChosen: (SinglesLevel) -> Unit) {
  Surface(
    modifier = Modifier.fillMaxWidth().clickable { onLevelChosen(level) },
    color = ShuuenUi.Panel,
    contentColor = ShuuenUi.Text,
    shape = MaterialTheme.shapes.medium,
    border = BorderStroke(1.dp, ShuuenUi.Border),
    tonalElevation = 0.dp,
    shadowElevation = 0.dp,
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
      val compact = maxWidth < 430.dp
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          LevelText(level.name)
          LevelParameterGrid(level = level)
          when (val first = level.traningScales.first().itemStates) {
            is TrainingScaleItemStates.ByDegree -> {
              BoxedItemRow(first.items, itemSize = 32.dp)
            }

            is TrainingScaleItemStates.ByPitch -> {
              BoxedItemRow(first.items, itemSize = 32.dp)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LevelText(title: String, modifier: Modifier = Modifier) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

@Composable
private fun LevelParameterGrid(
  level: SinglesLevel, modifier: Modifier = Modifier
) {
  val items = listOf(
    Triple(
      level.questionsNumber?.toString() ?: "Unlimited",
      Icons.AutoMirrored.Rounded.HelpOutline,
      1.25f
    ),
    Triple(level.range.toList().joinToString(" - "), Icons.Rounded.Keyboard, 1f),
  )

  Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier) {
    items.chunked(2).forEach { row ->
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        row.forEach { (text, icon, _) ->
          LevelParameter(text, icon, Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun LevelParameter(
  text: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
) {
  SoftControl(
    modifier = modifier.height(42.dp), selected = false
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = ShuuenUi.Lavender,
      modifier = Modifier.size(18.dp),
    )
    Text(
      text = text,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyLarge,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

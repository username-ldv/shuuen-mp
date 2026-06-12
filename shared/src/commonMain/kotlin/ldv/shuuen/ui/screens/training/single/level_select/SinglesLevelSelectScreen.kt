package ldv.shuuen.ui.screens.training.single.level_select

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.singles.SinglesLevel
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.SurfaceCard
import ldv.shuuen.ui.common.music.BoxedItemRow
import ldv.shuuen.ui.screens.training.common.toBoxedItems

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
        is ResponseState.Loading -> item {
          Text(
            text = "Loading...",
            color = ShuuenUi.Muted,
            style = MaterialTheme.typography.bodyLarge,
          )
        }

        is ResponseState.Success -> l.result.forEach { level ->
          item {
            LevelCard(level, onLevelChosen = { onStartLevel(it.id) })
          }
        }

        is ResponseState.Error -> item {
          Text(
            text = "Error loading levels: ${l.throwable.message}",
            color = ShuuenUi.Incorrect,
            style = MaterialTheme.typography.bodyLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun LevelCard(level: SinglesLevel, onLevelChosen: (SinglesLevel) -> Unit) {
  SurfaceCard(
    onClick = { onLevelChosen(level) },
    verticalSpacing = Arrangement.spacedBy(12.dp),
  ) {
    Text(
      text = level.name,
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.titleMedium.copy(
        letterSpacing = ShuuenUi.titlesSpacing,
        fontWeight = FontWeight.SemiBold,
      ),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    LevelParameterRow(level = level)
    when (val levelConfig = level.levelConfig) {
      is LevelConfig.Singles.Absolute -> {
        BoxedItemRow(levelConfig.scales.first().pitchStates.toBoxedItems(), itemSize = 32.dp)
      }

      is LevelConfig.Singles.Relative -> {
        BoxedItemRow(levelConfig.scaleConfig.degreeStates.toBoxedItems(), itemSize = 32.dp)
      }
    }
  }
}

@Composable
private fun LevelParameterRow(
  level: SinglesLevel, modifier: Modifier = Modifier
) {
  val items = listOf(
    (level.questionsNumber?.let { "$it questions" } ?: "Unlimited") to
      Icons.AutoMirrored.Rounded.HelpOutline,
    level.range.toPair().toList().joinToString(" - ") to Icons.Rounded.Keyboard,
  )

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    items.forEach { (text, icon) ->
      LevelParameter(text, icon)
    }
  }
}

@Composable
private fun LevelParameter(
  text: String,
  icon: ImageVector,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = ShuuenUi.Dim,
      modifier = Modifier.size(16.dp),
    )
    Text(
      text = text,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyMedium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

package ldv.shuuen.ui.screens.training.single.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.SectionTitle
import ldv.shuuen.ui.common.SegmentedPlusMinus
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.NoteRow
import ldv.shuuen.ui.common.music.ScaleChooser

@Composable
fun SinglesSetupScreen(
  viewModel: SinglesSetupScreenViewModel,
  onNavigateBack: () -> Unit,
  onOpenContext: () -> Unit,
  onSaveLevel: () -> Unit,
) {
  val saveableScreenState by viewModel.screenState.collectAsStateWithLifecycle()
  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "SINGLES SETUP",
        subtitle = "Create a custom training level.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled
      )
    },
  ) {

    val config = when (val levelConfig = saveableScreenState.levelConfig) {
      is LevelConfig.Singles.Relative -> levelConfig.scaleConfig
      is LevelConfig.Singles.Absolute -> levelConfig.scales.first()
    }

    ScaleChooser(
      scaleConfig = config,
      onScaleChosen = viewModel::changeScale
    )

    CompactSetupRow(
      icon = Icons.Rounded.Tune,
      tint = ShuuenUi.Lavender,
      title = "2. CONTEXT",
      subtitle = "Open context screen to configure.",
      trailing = true,
      onClick = onOpenContext,
    )

    GlassPanel {
      SectionTitle(
        icon = Icons.Rounded.BarChart,
        tint = ShuuenUi.Gold,
        title = "3. NUMBER OF QUESTIONS",
        subtitle = "Set how many questions to include.",
      )
      SegmentedPlusMinus(
        value = saveableScreenState.questionsNumber,
        onChange = viewModel::changeQuestionsNumber,
        minimalNumber = 0
      )
    }

    GlassPanel {
      SectionTitle(
        icon = Icons.Rounded.GraphicEq,
        title = "4. RANGE",
        subtitle = "Select the note range.",
      )
      Text(
        text = "${saveableScreenState.range.from} - ${saveableScreenState.range.to}",
        style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 3.sp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
      )
      NoteRow(value = saveableScreenState.range.from) { viewModel.changeRangeStart(it) }
      NoteRow(value = saveableScreenState.range.to) { viewModel.changeRangeEnd(it) }
    }

    val scope = rememberCoroutineScope()
    PrimaryCta(
      text = "SAVE LEVEL",
      onClick = {
        // todo: maybe add loading state
        scope.launch {
          viewModel.upsertLevel()
          onSaveLevel()
        }
      },
      modifier = Modifier.padding(top = 10.dp, bottom = 18.dp),
      icon = Icons.Rounded.Save
    )
  }
}

@Composable
private fun CompactSetupRow(
  icon: ImageVector,
  tint: Color,
  title: String,
  subtitle: String,
  trailing: Boolean = false,
  onClick: (() -> Unit)? = null,
) {
  GlassPanel(
    modifier = Modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      IconBubble(icon, tint = tint, size = 58.dp)
      Column(
        modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Text(
          text = title,
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.titleLarge.copy(
            letterSpacing = 3.sp, fontWeight = FontWeight.Bold
          ),
        )
        Text(
          subtitle, color = ShuuenUi.Muted, style = MaterialTheme.typography.bodyMedium
        )
      }
      if (trailing) {
        Icon(
          Icons.Rounded.ChevronRight,
          contentDescription = null,
          tint = ShuuenUi.Muted,
          modifier = Modifier.size(34.dp)
        )
      }
    }
  }
}

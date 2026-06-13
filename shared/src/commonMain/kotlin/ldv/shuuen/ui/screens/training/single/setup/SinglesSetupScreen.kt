package ldv.shuuen.ui.screens.training.single.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.ui.common.FlatSection
import ldv.shuuen.ui.common.Hairline
import ldv.shuuen.ui.common.PrimaryCta
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
      verticalSpacing = 22.dp,
      topBar = {
        ShuuenTopAppBar(
            title = "SINGLES SETUP",
            subtitle = "Create a custom training level.",
            onBack = onNavigateBack,
            type = ShuuenTopAppBarType.Labeled,
        )
      },
  ) {
    val config =
        when (val levelConfig = saveableScreenState.levelConfig) {
          is LevelConfig.Singles.Relative -> levelConfig.scaleConfig
          is LevelConfig.Singles.Absolute -> levelConfig.scales.first()
        }

    ScaleChooser(
        scaleConfig = config,
        onScaleChosen = viewModel::changeScale,
    )

    Hairline()

    NavigationSectionRow(
        label = "2 · CONTEXT",
        supporting =
            saveableScreenState.context?.let { "Using context ${it.id}" }
                ?: "Open context screen to configure.",
        onClick = onOpenContext,
    )

    Hairline()

    FlatSection(
        label = "3 · NUMBER OF QUESTIONS",
        supporting = "Set how many questions to include.",
    ) {
      SegmentedPlusMinus(
          value = saveableScreenState.questionsNumber,
          onChange = viewModel::changeQuestionsNumber,
          minimalNumber = 0,
      )
    }

    Hairline()

    FlatSection(
        label = "4 · RANGE",
        supporting = "Select the note range.",
    ) {
      Text(
          text = "${saveableScreenState.range.from} - ${saveableScreenState.range.to}",
          style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 3.sp),
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
        icon = Icons.Rounded.Save,
    )
  }
}

@Composable
private fun NavigationSectionRow(
    label: String,
    supporting: String,
    onClick: () -> Unit,
) {
  Row(
      modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(3.dp),
    ) {
      Text(
          text = label,
          color = ShuuenUi.Muted,
          style =
              MaterialTheme.typography.labelLarge.copy(
                  letterSpacing = ShuuenUi.labelSpacing,
                  fontWeight = FontWeight.SemiBold,
              ),
      )
      Text(
          text = supporting,
          color = ShuuenUi.Dim,
          style = MaterialTheme.typography.bodyMedium,
      )
    }
    Icon(
        Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = ShuuenUi.Dim,
        modifier = Modifier.size(26.dp),
    )
  }
}

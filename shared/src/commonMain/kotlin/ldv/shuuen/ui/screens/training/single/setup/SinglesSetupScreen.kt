package ldv.shuuen.ui.screens.training.single.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.PillControl
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.SectionTitle
import ldv.shuuen.ui.common.SegmentedPlusMinus
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.NoteRow

@Composable
fun SinglesSetupScreen(
  viewModel: SinglesSetupViewModel,
  onNavigateBack: () -> Unit,
  onOpenContext: () -> Unit,
  onStartTraining: () -> Unit,
) {
  val screenState by viewModel.screenState.collectAsStateWithLifecycle()
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

    GlassPanel {
      BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val compact = maxWidth < 430.dp
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 16.dp),
        ) {
          IconBubble(
            Icons.Rounded.MusicNote, tint = ShuuenUi.Mint, size = if (compact) 52.dp else 62.dp
          )
          Column(
            modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

            Row(
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              LabeledPicker("TONIC", "C", Modifier.weight(1f))
              LabeledPicker("MODE", "Major", Modifier.weight(1.35f))
            }
            ScaleChoiceGrid()
            PillControl(
              "More scales", leadingIcon = Icons.Rounded.Casino, modifier = Modifier.fillMaxWidth()
            )
            PillControl(
              "Custom scale", leadingIcon = Icons.Rounded.Edit, modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

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
        value = screenState.questionsNumber,
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
        text = "${screenState.range.first} - ${screenState.range.second}",
        style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 3.sp),
        modifier = Modifier.align(Alignment.CenterHorizontally),
      )
      NoteRow(value = screenState.range.first) { viewModel.changeRangeStart(it) }
      NoteRow(value = screenState.range.second) { viewModel.changeRangeEnd(it) }
    }

    PrimaryCta(
      text = "START TRAINING",
      onClick = onStartTraining,
      modifier = Modifier.padding(top = 10.dp, bottom = 18.dp),
    )
  }
}

@Composable
private fun ScaleChoiceGrid() {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      PillControl(
        "C Major", selected = true, trailingCheck = true, modifier = Modifier.weight(1f)
      )
      PillControl(
        "A Minor", modifier = Modifier.weight(1f)
      )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      PillControl(
        "G Major", modifier = Modifier.weight(1f)
      )
      PillControl(
        "E Minor", modifier = Modifier.weight(1f)
      )
    }
  }
}

@Composable
private fun LabeledPicker(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      label,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp)
    )
    PillControl(
      value, modifier = Modifier.fillMaxWidth()
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

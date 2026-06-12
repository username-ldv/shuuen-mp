package ldv.shuuen.ui.screens.training.melodies.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.domain.training.level.ScaleConfig
import ldv.shuuen.ui.common.FlatSection
import ldv.shuuen.ui.common.Hairline
import ldv.shuuen.ui.common.LinearTrainingProgress
import ldv.shuuen.ui.common.PillControl
import ldv.shuuen.ui.common.PrimaryCta
import ldv.shuuen.ui.common.SegmentedPlusMinus
import ldv.shuuen.ui.common.ShuuenSwitch
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import ldv.shuuen.ui.common.music.NoteRow
import ldv.shuuen.ui.common.music.ScaleChooser
import ldv.shuuen.ui.common.music.inputs.PianoKeyboard
import ldv.shuuen.ui.common.music.inputs.PianoKeyboardDefaults
import ldv.shuuen.ui.screens.training.common.asConfigDegreeStates

@Composable
fun MelodiesSetupScreen(
  onNavigateBack: () -> Unit,
  onOpenContext: () -> Unit,
  onStartTraining: () -> Unit,
) {
  StaticScreenFrame(
    maxWidth = 920.dp,
    verticalSpacing = 22.dp,
    topBar = {
      ShuuenTopAppBar(
        title = "MELODIES SETUP",
        subtitle = "Create a custom melody training level.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled,
      )
    },
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val twoColumn = maxWidth > 760.dp

      if (twoColumn) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(44.dp),
        ) {
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(22.dp),
          ) {
            LeadingSections(onOpenContext)
          }
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(22.dp),
          ) {
            TrailingSections()
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
          LeadingSections(onOpenContext)
          Hairline()
          TrailingSections()
        }
      }
    }

    PrimaryCta(
      text = "START TRAINING",
      onClick = onStartTraining,
      modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
    )
  }
}

@Composable
private fun LeadingSections(onOpenContext: () -> Unit) {
  MelodyScaleSection()
  Hairline()
  ContextSection(onOpenContext)
  Hairline()
  SourceModeSection()
  Hairline()
  QuestionCountSection()
}

@Composable
private fun TrailingSections() {
  NotesPerSequenceSection()
  Hairline()
  TempoSection()
  Hairline()
  RhythmSection()
  Hairline()
  MelodyRangeSection()
}

@Composable
private fun MelodyScaleSection() {
  // Local UI state only — saving comes with the melodies level functionality later.
  var scaleConfig: ScaleConfig by remember {
    mutableStateOf(
      ScaleConfig.RelativeScaleConfig(
        scaleType = ScaleType.Major,
        degreeStates = Scale.major(Pitch.C).asConfigDegreeStates(),
      )
    )
  }
  ScaleChooser(
    scaleConfig = scaleConfig,
    onScaleChosen = { scaleConfig = it },
  )
}

@Composable
private fun ContextSection(onOpenContext: () -> Unit) {
  SetupNavRow(
    label = "2 · CONTEXT",
    supporting = "Open context screen to configure.",
    onClick = onOpenContext,
  ) {
    Icon(
      Icons.Rounded.ChevronRight,
      contentDescription = null,
      tint = ShuuenUi.Dim,
      modifier = Modifier.size(26.dp),
    )
  }
}

@Composable
private fun SourceModeSection() {
  FlatSection(label = "3 · SOURCE MODE") {
    PillControl(
      text = "Random",
      selected = true,
      leadingIcon = Icons.Rounded.Casino,
      trailingCheck = true,
      modifier = Modifier.fillMaxWidth(),
    )
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      PillControl(
        text = "Load .midi file",
        leadingIcon = Icons.Rounded.FolderOpen,
        modifier = Modifier.weight(1f),
      )
      PillControl(
        text = "Open library",
        leadingIcon = Icons.Rounded.FolderOpen,
        modifier = Modifier.weight(1f),
      )
    }
    Text(
      text = "MIDI options are used when MIDI source is selected.",
      color = ShuuenUi.Dim,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun QuestionCountSection() {
  FlatSection(label = "4 · NUMBER OF QUESTIONS") {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      var questions: String by remember { mutableStateOf("20") }
      SegmentedPlusMinus(
        value = questions.toIntOrNull(),
        onChange = { questions = it.toString() },
        modifier = Modifier.weight(1f)
      )
      Text(
        text = "∞",
        color = ShuuenUi.Muted,
        style = MaterialTheme.typography.headlineMedium,
      )
      ShuuenSwitch(checked = false)
    }
  }
}

@Composable
private fun NotesPerSequenceSection() {
  FlatSection(label = "5 · NOTES PER SEQUENCE") {
    NotesPerSequenceControls()
    SoftControl(modifier = Modifier.fillMaxWidth()) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          "Endless note mode",
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.titleSmall,
        )
        Text(
          "Ignore sequence length and keep playing.",
          color = ShuuenUi.Muted,
          style = MaterialTheme.typography.bodySmall,
        )
      }
      ShuuenSwitch(checked = false)
    }
  }
}

@Composable
private fun TempoSection() {
  FlatSection(
    label = "6 · TEMPO",
    supporting = "Used in Random mode.",
    trailing = {
      PillControl(
        text = "96 BPM",
        leadingIcon = Icons.Rounded.Edit,
        modifier = Modifier.width(130.dp),
      )
    },
  ) {
    LinearTrainingProgress(progress = 0.38f)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        "60",
        color = ShuuenUi.Dim,
        style = MaterialTheme.typography.bodySmall
      )
      Text(
        "180",
        color = ShuuenUi.Dim,
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}

@Composable
private fun RhythmSection() {
  SetupNavRow(
    label = "7 · RHYTHM",
    supporting = "Configure rhythm patterns. Used in Random mode only.",
  ) {
    ShuuenSwitch(checked = true)
    Icon(
      Icons.Rounded.ChevronRight,
      contentDescription = null,
      tint = ShuuenUi.Dim,
      modifier = Modifier.size(26.dp),
    )
  }
}

@Composable
private fun MelodyRangeSection() {
  FlatSection(
    label = "8 · RANGE",
    supporting = "Select the note range.",
  ) {
    Text(
      text = "C3 - C5",
      style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = 3.sp),
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    NoteRow(value = Note(Pitch.C, 3)) {}
    NoteRow(value = Note(Pitch.C, 5)) {}
  }
}

@Composable
private fun NotesPerSequenceControls(modifier: Modifier = Modifier) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    listOf("2", "4", "8", "12").forEach { value ->
      PillControl(
        text = value,
        selected = value == "4",
        modifier = Modifier.weight(1f),
      )
    }
    Text("Custom", color = ShuuenUi.Dim, style = MaterialTheme.typography.bodySmall)
    SoftControl(
      modifier = Modifier.width(54.dp),
      selected = false
    ) {
      Text(
        "4",
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.weight(1f),
        maxLines = 1,
      )
    }
  }
}

@Composable
private fun MelodyRangeKeyboardStrip() {
  val keyCount = 36
  val selectedRange = 12..30

  PianoKeyboard(
    modifier = Modifier
      .fillMaxWidth()
      .aspectRatio(PianoKeyboardDefaults.aspectRatio(keyCount)),
    keyCount = keyCount,
    idleKeyColors = List(keyCount) { index ->
      when {
        index !in selectedRange -> if (PianoKeyboardDefaults.isBlackKey(index)) {
          Color(0xFF151515)
        } else {
          Color(0xFF2A2A2A)
        }

        PianoKeyboardDefaults.isBlackKey(index) -> Color(0xFF4D4D4D)
        else -> Color(0xFFE8E8E8)
      }
    },
    borderWidth = 1.dp,
    separatorWidth = 1.dp,
    whiteKeyCornerRadius = 3.dp,
    blackKeyCornerRadius = 2.dp,
  )
}

@Composable
private fun SetupNavRow(
  label: String,
  supporting: String,
  onClick: (() -> Unit)? = null,
  trailing: @Composable RowScope.() -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth()
      .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
      Text(
        text = label,
        color = ShuuenUi.Muted,
        style = MaterialTheme.typography.labelLarge.copy(
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
    trailing()
  }
}

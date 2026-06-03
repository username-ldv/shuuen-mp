package ldv.shuuen.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.music.PianoKeyboard
import ldv.shuuen.ui.music.PianoKeyboardDefaults

@Composable
fun MelodiesSetupScreen(
  onNavigateBack: () -> Unit,
  onStartTraining: () -> Unit,
) {
  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        title = "MELODIES SETUP",
        subtitle = "Create a custom melody training level.",
        onBack = onNavigateBack,
        type = ShuuenTopAppBarType.Labeled,
      )
    },
  ) {
    MelodyScaleSection()

    MelodySetupRow(
      icon = Icons.Rounded.Tune,
      tint = ShuuenUi.Lavender,
      title = "2. CONTEXT",
      subtitle = "Open context screen to configure.",
      trailing = true,
    )

    SourceModeSection()
    QuestionCountSection()
    NotesPerSequenceSection()
    TempoSection()
    RhythmSection()
    MelodyRangeSection()

    PrimaryCta(
      text = "START TRAINING",
      onClick = onStartTraining,
      modifier = Modifier.padding(top = 4.dp, bottom = 18.dp),
    )
  }
}

@Composable
private fun MelodyScaleSection() {
  GlassPanel {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 430.dp

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 16.dp),
      ) {
        IconBubble(
          icon = Icons.Rounded.MusicNote,
          tint = ShuuenUi.Mint,
          size = if (compact) 52.dp else 62.dp,
        )
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Column(
              modifier = Modifier.weight(1f),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              SetupTitle("1. SCALE")
              Text(
                "Choose the scale you want to train.",
                color = ShuuenUi.Muted,
                style = MaterialTheme.typography.bodyMedium,
              )
            }
            Icon(
              Icons.Rounded.ExpandLess,
              contentDescription = null,
              tint = ShuuenUi.Muted,
              modifier = Modifier.size(30.dp),
            )
          }

          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            LabeledPicker("TONIC", "C", Modifier.weight(1f))
            LabeledPicker("MODE", "Major", Modifier.weight(1.35f))
          }
          ScaleChoiceGrid()
          PillControl(
            "More scales",
            leadingIcon = Icons.Rounded.Casino,
            modifier = Modifier.fillMaxWidth(),
          )
          PillControl(
            "Custom scale",
            leadingIcon = Icons.Rounded.Edit,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }
    }
  }
}

@Composable
private fun SourceModeSection() {
  GlassPanel {
    SectionTitle(
      icon = Icons.AutoMirrored.Rounded.QueueMusic,
      title = "3. SOURCE MODE",
      subtitle = "",
    )
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
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun QuestionCountSection() {
  MelodySetupRow(
    icon = Icons.Rounded.BarChart,
    tint = ShuuenUi.Gold,
    title = "4. NUMBER OF QUESTIONS",
    subtitle = "",
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      CounterControl("20", Modifier.weight(1f))
      Text(
        text = "∞",
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.headlineLarge,
      )
      ShuuenSwitch(checked = false)
    }
  }
}

@Composable
private fun NotesPerSequenceSection() {
  GlassPanel {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 460.dp
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (compact) {
          SectionTitle(
            icon = Icons.Rounded.MusicNote,
            title = "5. NOTES PER SEQUENCE",
            subtitle = "",
          )
          NotesPerSequenceControls()
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            IconBubble(Icons.Rounded.MusicNote, tint = ShuuenUi.Lavender, size = 50.dp)
            SetupTitle("5. NOTES PER SEQUENCE", Modifier.weight(1f))
            NotesPerSequenceControls(Modifier.weight(1.2f))
          }
        }

        SoftControl(modifier = Modifier.fillMaxWidth()) {
          Icon(
            Icons.Rounded.GraphicEq,
            contentDescription = null,
            tint = ShuuenUi.Text,
            modifier = Modifier.size(24.dp),
          )
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
  }
}

@Composable
private fun TempoSection() {
  GlassPanel {
    SectionTitle(
      icon = Icons.Rounded.PlayArrow,
      tint = ShuuenUi.Mint,
      title = "6. TEMPO",
      subtitle = "Used in Random mode.",
      trailing = {
        PillControl(
          text = "96 BPM",
          leadingIcon = Icons.Rounded.Edit,
          modifier = Modifier.width(150.dp),
        )
      },
    )
    Text(
      text = "96 BPM",
      color = ShuuenUi.Mint,
      style = MaterialTheme.typography.titleSmall,
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    LinearTrainingProgress(progress = 0.38f, color = ShuuenUi.Mint)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("60", color = ShuuenUi.Muted, style = MaterialTheme.typography.bodySmall)
      Text("180", color = ShuuenUi.Muted, style = MaterialTheme.typography.bodySmall)
    }
  }
}

@Composable
private fun RhythmSection() {
  MelodySetupRow(
    icon = Icons.AutoMirrored.Rounded.QueueMusic,
    tint = ShuuenUi.Lavender,
    title = "7. RHYTHM",
    subtitle = "Configure rhythm patterns\nUsed in Random mode only.",
    trailing = true,
  ) {
    ShuuenSwitch(checked = true)
  }
}

@Composable
private fun MelodyRangeSection() {
  GlassPanel {
    SectionTitle(
      icon = Icons.Rounded.GraphicEq,
      title = "8. RANGE",
      subtitle = "Select the note range.",
    )
    Text(
      text = "C3 - C5",
      color = ShuuenUi.Text,
      style = MaterialTheme.typography.headlineLarge.copy(letterSpacing = 3.sp),
      modifier = Modifier.align(Alignment.CenterHorizontally),
    )
    MelodyRangeKeyboardStrip()
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      listOf("C2", "C3", "C4", "C5", "C6").forEach {
        Text(it, color = ShuuenUi.Muted, style = MaterialTheme.typography.bodySmall)
      }
    }
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
        trailingCheck = value == "4",
        modifier = Modifier.weight(1f),
      )
    }
    Text("Custom", color = ShuuenUi.Mint, style = MaterialTheme.typography.bodySmall)
    SoftControl(modifier = Modifier.width(54.dp), selected = false) {
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
      .aspectRatio(PianoKeyboardDefaults.aspectRatio(keyCount))
      .border(1.dp, ShuuenUi.Border, RoundedCornerShape(8.dp)),
    keyCount = keyCount,
    idleKeyColors = List(keyCount) { index ->
      when {
        index !in selectedRange -> if (PianoKeyboardDefaults.isBlackKey(index)) {
          Color(0xFF151515)
        } else {
          Color(0xFF292929)
        }

        PianoKeyboardDefaults.isBlackKey(index) -> Color(0xFF443F55)
        else -> Color(0xFFE1D6FF)
      }
    },
    borderWidth = 1.dp,
    separatorWidth = 1.dp,
    whiteKeyCornerRadius = 3.dp,
    blackKeyCornerRadius = 2.dp,
  )
}

@Composable
private fun ScaleChoiceGrid() {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      PillControl("C Major", selected = true, trailingCheck = true, modifier = Modifier.weight(1f))
      PillControl("A Minor", modifier = Modifier.weight(1f))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
      PillControl("G Major", modifier = Modifier.weight(1f))
      PillControl("E Minor", modifier = Modifier.weight(1f))
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
      style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
    )
    PillControl(value, modifier = Modifier.fillMaxWidth())
  }
}

@Composable
private fun MelodySetupRow(
  icon: ImageVector,
  tint: Color,
  title: String,
  subtitle: String,
  trailing: Boolean = false,
  extraContent: @Composable (() -> Unit)? = null,
) {
  GlassPanel {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      IconBubble(icon, tint = tint, size = 58.dp)
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        SetupTitle(title)
        if (subtitle.isNotBlank()) {
          Text(
            subtitle,
            color = ShuuenUi.Muted,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
      extraContent?.invoke()
      if (trailing) {
        Icon(
          Icons.Rounded.ChevronRight,
          contentDescription = null,
          tint = ShuuenUi.Muted,
          modifier = Modifier.size(34.dp),
        )
      }
    }
  }
}

@Composable
private fun SetupTitle(
  text: String,
  modifier: Modifier = Modifier,
) {
  Text(
    text = text,
    color = ShuuenUi.Text,
    style = MaterialTheme.typography.titleLarge.copy(
      letterSpacing = ShuuenUi.titlesSpacing,
      fontWeight = FontWeight.Bold,
    ),
    modifier = modifier,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
  )
}

@Composable
private fun ShuuenSwitch(checked: Boolean) {
  Switch(
    checked = checked,
    onCheckedChange = {},
    colors = SwitchDefaults.colors(
      checkedThumbColor = ShuuenUi.Text,
      checkedTrackColor = Color(0xFF6CA58D),
      uncheckedThumbColor = ShuuenUi.Text,
      uncheckedTrackColor = Color(0xFF171717),
      uncheckedBorderColor = Color.Transparent,
    ),
  )
}

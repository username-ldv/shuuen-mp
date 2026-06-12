package ldv.shuuen.ui.screens.app_settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ldv.shuuen.ui.common.FlatSection
import ldv.shuuen.ui.common.Hairline
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.PillControl
import ldv.shuuen.ui.common.ShuuenSwitch
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
  StaticScreenFrame(
    maxWidth = 920.dp,
    topBar = {
      ShuuenTopAppBar(
        title = "SETTINGS",
        onBack = onNavigateBack,
        trailingIcon = Icons.Rounded.Tune,
        type = ShuuenTopAppBarType.Simple
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
            verticalArrangement = Arrangement.spacedBy(26.dp),
          ) {
            InputMethodSection()
            Hairline()
            GeneralSection()
          }
          Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(26.dp),
          ) {
            SoundfontSection()
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(26.dp),
        ) {
          InputMethodSection()
          Hairline()
          SoundfontSection()
          Hairline()
          GeneralSection()
        }
      }
    }

    Text(
      text = "Changes are applied automatically.",
      color = ShuuenUi.Dim,
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp, bottom = 18.dp),
      textAlign = TextAlign.Center,
    )
  }
}

@Composable
private fun InputMethodSection() {
  FlatSection(
    label = "INPUT METHOD",
    supporting = "Choose how answers are entered and interpreted.",
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 390.dp
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          InputMethodCard(
            "Piano",
            "Absolute",
            Icons.Rounded.Keyboard,
            true,
            compact,
            Modifier.weight(1f)
          )
          InputMethodCard(
            "Piano",
            "Relative",
            Icons.Rounded.Keyboard,
            false,
            compact,
            Modifier.weight(1f)
          )
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          InputMethodCard(
            "Circle",
            "Absolute",
            Icons.Rounded.GraphicEq,
            false,
            compact,
            Modifier.weight(1f)
          )
          InputMethodCard(
            "Circle",
            "Relative",
            Icons.Rounded.GraphicEq,
            false,
            compact,
            Modifier.weight(1f)
          )
        }
      }
    }
  }
}

@Composable
private fun SoundfontSection() {
  FlatSection(
    label = "SOUNDFONT",
    supporting = "Use one MIDI soundfont for all playback categories.",
  ) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 440.dp
      if (compact) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          SoftControl(modifier = Modifier.fillMaxWidth()) {
            Icon(
              Icons.AutoMirrored.Rounded.Article,
              contentDescription = null,
              tint = ShuuenUi.Muted,
              modifier = Modifier.size(22.dp)
            )
            Text(
              "Arachno.sf2",
              color = ShuuenUi.Text,
              style = MaterialTheme.typography.titleSmall,
              modifier = Modifier.weight(1f),
              maxLines = 1
            )
          }
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            PillControl(
              "Load",
              leadingIcon = Icons.Rounded.FolderOpen,
              selected = true,
              modifier = Modifier.weight(1f)
            )
            PillControl("Default", modifier = Modifier.weight(1f))
          }
        }
      } else {
        SoftControl(modifier = Modifier.fillMaxWidth()) {
          Icon(
            Icons.AutoMirrored.Rounded.Article,
            contentDescription = null,
            tint = ShuuenUi.Muted,
            modifier = Modifier.size(24.dp)
          )
          Text(
            "Arachno.sf2",
            color = ShuuenUi.Text,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1
          )
          PillControl(
            "Load from storage",
            leadingIcon = Icons.Rounded.FolderOpen,
            selected = true
          )
          PillControl("Default")
        }
      }
    }
    SoundCategoryRow(
      "Notes",
      Icons.Rounded.MusicNote,
      "000 - General MIDI",
      "001 - Acoustic Grand"
    )
    Hairline()
    SoundCategoryRow("Drone", Icons.Rounded.Waves, "048 - Ethnic", "045 - Shakuhachi")
    Hairline()
    SoundCategoryRow(
      "Cadence",
      Icons.Rounded.GraphicEq,
      "000 - General MIDI",
      "024 - Nylon Guitar"
    )
  }
}

@Composable
private fun GeneralSection() {
  FlatSection(label = "GENERAL") {
    SettingsRow(Icons.Rounded.Language, "Language", trailing = "English")
    Hairline()
    SettingsRow(Icons.Rounded.TextFields, "Note names", subtitle = "C, D, E...")
    Hairline()
    SettingsRow(Icons.Rounded.TextFields, "Degree names", subtitle = "1, 2, 3...")
    Hairline()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
      Icon(
        Icons.Rounded.PlayArrow,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(22.dp)
      )
      Text(
        text = "Play next question automatically",
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.weight(1f),
      )
      ShuuenSwitch(checked = true)
    }
  }
}

@Composable
private fun InputMethodCard(
  title: String,
  mode: String,
  icon: ImageVector,
  selected: Boolean,
  compact: Boolean,
  modifier: Modifier = Modifier,
) {
  SoftControl(
    modifier = modifier.heightIn(min = if (compact) 92.dp else 86.dp),
    selected = selected,
  ) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Icon(
          icon,
          contentDescription = null,
          tint = if (selected) ShuuenUi.Text else ShuuenUi.Muted,
          modifier = Modifier.size(if (compact) 22.dp else 24.dp)
        )
        Spacer(Modifier.weight(1f))
        Icon(
          imageVector = if (selected) Icons.Rounded.Check else Icons.Rounded.RadioButtonUnchecked,
          contentDescription = null,
          tint = if (selected) ShuuenUi.Text else ShuuenUi.Dim,
          modifier = Modifier.size(if (compact) 20.dp else 22.dp),
        )
      }
      Text(
        text = title,
        color = if (selected) ShuuenUi.Text else ShuuenUi.Muted,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
      Text(
        text = mode,
        color = if (selected) ShuuenUi.Muted else ShuuenUi.Dim,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
      )
    }
  }
}

@Composable
private fun SoundCategoryRow(
  label: String,
  icon: ImageVector,
  soundbank: String,
  preset: String,
) {
  BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    val compact = maxWidth < 480.dp

    if (compact) {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          Icon(
            icon,
            contentDescription = null,
            tint = ShuuenUi.Muted,
            modifier = Modifier.size(22.dp)
          )
          Text(
            text = label,
            color = ShuuenUi.Text,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.weight(1f),
          )
          IconBubble(Icons.Rounded.PlayArrow, tint = ShuuenUi.Text, size = 36.dp)
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          SoundPickerColumn("SOUNDBANK", soundbank, Modifier.weight(1f))
          SoundPickerColumn("PRESET", preset, Modifier.weight(1f))
        }
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Icon(
          icon,
          contentDescription = null,
          tint = ShuuenUi.Muted,
          modifier = Modifier.size(22.dp)
        )
        Text(
          text = label,
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          modifier = Modifier.width(74.dp),
        )
        SoundPickerColumn("SOUNDBANK", soundbank, Modifier.weight(1f))
        SoundPickerColumn("PRESET", preset, Modifier.weight(1f))
        IconBubble(Icons.Rounded.PlayArrow, tint = ShuuenUi.Text, size = 40.dp)
      }
    }
  }
}

@Composable
private fun SoundPickerColumn(
  label: String,
  value: String,
  modifier: Modifier = Modifier,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      label,
      color = ShuuenUi.Dim,
      style = MaterialTheme.typography.labelSmall.copy(letterSpacing = ShuuenUi.labelSpacing),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
    PillControl(value)
  }
}

@Composable
private fun SettingsRow(
  icon: ImageVector,
  title: String,
  subtitle: String? = null,
  trailing: String? = null,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    Icon(icon, contentDescription = null, tint = ShuuenUi.Muted, modifier = Modifier.size(22.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        color = ShuuenUi.Text,
        style = MaterialTheme.typography.titleMedium,
      )
      if (subtitle != null) {
        Text(text = subtitle, color = ShuuenUi.Dim, style = MaterialTheme.typography.bodySmall)
      }
    }
    if (trailing != null) {
      PillControl(trailing, modifier = Modifier.width(170.dp))
    } else {
      Icon(
        Icons.Rounded.ChevronRight,
        contentDescription = null,
        tint = ShuuenUi.Dim,
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

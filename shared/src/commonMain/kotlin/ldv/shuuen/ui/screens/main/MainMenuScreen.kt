package ldv.shuuen.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.LibraryBooks
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material.icons.rounded.ModeNight
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.common.GlassPanel
import ldv.shuuen.ui.common.IconBubble
import ldv.shuuen.ui.common.LinearTrainingProgress
import ldv.shuuen.ui.common.ShuuenTopAppBar
import ldv.shuuen.ui.common.ShuuenTopAppBarType
import ldv.shuuen.ui.common.ShuuenUi
import ldv.shuuen.ui.common.SoftControl
import ldv.shuuen.ui.common.StaticScreenFrame
import org.jetbrains.compose.resources.painterResource
import shuuen.shared.generated.resources.Res
import shuuen.shared.generated.resources.shuuen_main_logo

@Composable
fun MainMenuScreen(
  onOpenFreePlay: () -> Unit,
  onOpenMelodies: () -> Unit,
  onOpenSingles: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  StaticScreenFrame(
    topBar = {
      ShuuenTopAppBar(
        trailingIcon = Icons.Rounded.Settings,
        onTrailingClick = onOpenSettings,
        type = ShuuenTopAppBarType.Simple
      )
    },
  ) {
    Box(
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Image(
          painter = painterResource(Res.drawable.shuuen_main_logo),
          contentDescription = "Shuuen",
          modifier = Modifier.fillMaxWidth(0.68f),
          contentScale = ContentScale.Fit,
        )
        Text(
          text = "Last ear trainer you need.",
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.titleMedium,
          textAlign = TextAlign.Center,
        )
      }
    }

    ContinuePanel()

    MenuExerciseRow(
      title = "SINGLES",
      subtitle = "Identify single notes / degrees.",
      icon = Icons.Rounded.MusicNote,
      tint = ShuuenUi.Mint,
      onClick = onOpenSingles,
    )
    MenuExerciseRow(
      title = "MELODIES",
      subtitle = "Transcribe melodies.",
      icon = Icons.AutoMirrored.Rounded.QueueMusic,
      tint = Color(0xFFA9E8EC),
      onClick = onOpenMelodies,
    )
    MenuExerciseRow(
      title = "CHORDS",
      subtitle = "Identify single chords.",
      icon = Icons.Rounded.GraphicEq,
      tint = ShuuenUi.Lavender,
    )
    MenuExerciseRow(
      title = "PROGRESSIONS",
      subtitle = "Identify chord progressions.",
      icon = Icons.Rounded.BarChart,
      tint = ShuuenUi.Gold,
    )
    MenuExerciseRow(
      title = "FREE PLAY",
      subtitle = "Play freely without scoring.",
      icon = Icons.Rounded.Keyboard,
      tint = ShuuenUi.Lavender,
      onClick = onOpenFreePlay,
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      BottomPill(
        "LIBRARY", Icons.AutoMirrored.Rounded.LibraryBooks, ShuuenUi.Lavender, Modifier.weight(1f)
      )
      BottomPill("STATISTICS", Icons.Rounded.BarChart, ShuuenUi.Mint, Modifier.weight(1f))
      BottomPill("POCKET", Icons.Rounded.ModeNight, ShuuenUi.Lavender, Modifier.weight(1f))
    }
  }
}

@Composable
private fun ContinuePanel() {
  GlassPanel {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
      val compact = maxWidth < 410.dp
      val progressSize = if (compact) 88.dp else 104.dp
      val iconSize = if (compact) 72.dp else 86.dp

      if (compact) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
          ContinueTextBlock()
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(18.dp),
        ) {
          ContinueTextBlock(modifier = Modifier.weight(1f))
        }
      }
    }
  }
}

@Composable
private fun ContinueTextBlock(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier,
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        Text(
          text = "CONTINUE LAST SESSION",
          color = ShuuenUi.Mint,
          style = MaterialTheme.typography.titleMedium.copy(
            letterSpacing = ShuuenUi.titlesSpacing,
            fontWeight = FontWeight.Bold,
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = "Singles - D Major - 20 questions",
          color = ShuuenUi.Muted,
          style = MaterialTheme.typography.bodyLarge,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
        )
      }

      Icon(
        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(38.dp),
      )
    }
    LinearTrainingProgress(progress = 0.68f, color = ShuuenUi.Mint)
    Text(
      text = "68% complete",
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.bodyLarge,
      maxLines = 1,
    )
  }
}

@Composable
private fun MenuExerciseRow(
  title: String,
  subtitle: String,
  icon: ImageVector,
  tint: Color,
  onClick: (() -> Unit)? = null,
) {
  GlassPanel(
    modifier = Modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      IconBubble(icon = icon, tint = tint, size = 64.dp)
      Column(
        modifier = Modifier.weight(1f),
      ) {
        Text(
          text = title,
          color = ShuuenUi.Text,
          style = MaterialTheme.typography.displaySmall.copy(
            fontSize = 18.sp,
            lineHeight = 30.sp,
            letterSpacing = ShuuenUi.titlesSpacing,
            fontWeight = FontWeight.Bold,
          ),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = subtitle,
          color = ShuuenUi.Muted,
          style = MaterialTheme.typography.titleSmall,
        )
      }
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        contentDescription = null,
        tint = ShuuenUi.Muted,
        modifier = Modifier.size(38.dp),
      )
    }
  }
}

@Composable
private fun BottomPill(
  text: String,
  icon: ImageVector,
  tint: Color,
  modifier: Modifier = Modifier,
) {
  SoftControl(modifier = modifier) {
    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    Text(
      text = text,
      color = ShuuenUi.Muted,
      style = MaterialTheme.typography.labelMedium,
      maxLines = 1,
    )
  }
}

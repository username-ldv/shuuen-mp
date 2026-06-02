package ldv.shuuen.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
  onOpenSingles: () -> Unit,
  onOpenSettings: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    Text(
      text = "Shuuen",
      style = MaterialTheme.typography.displayLarge,
      textAlign = TextAlign.Center,
    )
    Text(
      text = "The last ear trainer app you need.",
      style = MaterialTheme.typography.labelMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )

    Spacer(Modifier.height(32.dp))

    Button(
      onClick = onOpenSingles,
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 420.dp),
    ) {
      Text("Singles")
    }

    OutlinedButton(
      onClick = onOpenSettings,
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 420.dp),
    ) {
      Text("Settings")
    }
  }
}

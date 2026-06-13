package ldv.shuuen.ui.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.ui.theme.ShuuenTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDropdownMenu(
  items: List<String>,
  selectedItem: String,
  onItemSelected: (String) -> Unit,
  modifier: Modifier = Modifier,
  label: String? = null,
) {
  var expanded by rememberSaveable { mutableStateOf(false) }
  val focusManager = LocalFocusManager.current

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = it },
    modifier = modifier,
  ) {
    OutlinedTextField(
      value = selectedItem,
      onValueChange = {},
      enabled = false,
      shape = ShuuenUi.ControlShape,
      colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
        disabledTextColor = ShuuenUi.Text,
        disabledBorderColor = ShuuenUi.HairlineStrong,
        disabledTrailingIconColor = ShuuenUi.Muted,
        disabledLabelColor = ShuuenUi.Muted,
        disabledPlaceholderColor = ShuuenUi.Muted,
      ),
      readOnly = true,
      singleLine = true,
      trailingIcon = {
        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
      },
      modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        .fillMaxWidth(),
    )

    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = {
        expanded = false
        focusManager.clearFocus()
      },
      modifier = Modifier.exposedDropdownSize(matchAnchorWidth = true),
    ) {
      items.forEach { item ->
        DropdownMenuItem(
          text = { Text(item) },
          onClick = {
            onItemSelected(item)
            expanded = false
            focusManager.clearFocus()
          },
          trailingIcon = {
            if (item == selectedItem) {
              Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
              )
            }
          },
        )
      }
    }
  }
}

@Preview
@Composable
private fun TextDropdownMenuPreview() {
  ShuuenTheme {
    TextDropdownMenu(
      items = Pitch.entries.map { it.toString() },
      selectedItem = Pitch.C.toString(),
      onItemSelected = {},
    )
  }
}

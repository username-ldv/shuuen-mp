package ldv.shuuen.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ldv.shuuen.ui.theme.ShuuenTheme

@Composable
fun SegmentedPlusMinus(
  value: Int?,
  modifier: Modifier = Modifier,
  onChange: (Int?) -> Unit = {},
  delta: Int = 5,
  minimalNumber: Int = 0
) {
  Row(
    modifier = Modifier.fillMaxWidth().height(58.dp).clip(MaterialTheme.shapes.extraLarge)
      .border(1.dp, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.shapes.extraLarge)
      .then(modifier),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    SegmentedPart("—", onClick = {
      onChange(value?.let {
        val v = it - delta
        if (v < minimalNumber) minimalNumber else v
      })
    })
    VerticalDivider(color = MaterialTheme.colorScheme.primaryContainer)
    Box(
      modifier = Modifier.fillMaxHeight().weight(1f), contentAlignment = Alignment.Center
    ) {
      BasicTextField(
        value = value?.toString() ?: "",
        onValueChange = { v ->
          val newValue = v.toUIntOrNull() ?: return@BasicTextField onChange(null)
          onChange(newValue.toInt())
        },
        textStyle = MaterialTheme.typography.headlineLarge.copy(
          color = LocalContentColor.current, textAlign = TextAlign.Center
        ),
        singleLine = true,
        cursorBrush = SolidColor(LocalContentColor.current),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      )
    }
    VerticalDivider(color = MaterialTheme.colorScheme.primaryContainer)
    SegmentedPart("+", onClick = {
      onChange(value?.let { it + delta })
    })
  }
}

@Composable
private fun RowScope.SegmentedPart(text: String, onClick: () -> Unit) {
  Box(
    modifier = Modifier.weight(0.45f).fillMaxHeight().clickable { onClick() },
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
    )
  }
}

@Preview
@Composable
fun SegmentedPlusMinusPreview() {
  ShuuenTheme {
    SegmentedPlusMinus(15)
  }
}

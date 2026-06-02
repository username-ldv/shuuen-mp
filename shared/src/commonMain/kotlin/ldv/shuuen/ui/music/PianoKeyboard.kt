package ldv.shuuen.ui.music

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ldv.shuuen.music.Pitch

@Immutable
data class PianoKeyIndication(
  val index: Int,
  /**
   * null = persistent while this object is present in programmaticIndications.
   * non-null = animate for this many milliseconds.
   */
  val durationMillis: Long? = null,
)

object PianoKeyboardDefaults {
  private val BlackPitchClasses = setOf(1, 3, 6, 8, 10)

  fun isBlackKey(index: Int): Boolean {
    val pitchClass = ((index % 12) + 12) % 12
    return pitchClass in BlackPitchClasses
  }

  fun idleColors(keyCount: Int): List<Color> = List(keyCount) { index ->
    if (isBlackKey(index)) Color.Black else Color(0xFFF2F2F2)
  }

  fun pressedColors(
    keyCount: Int,
    color: Color = Color(0xFFEFFF2B),
  ): List<Color> = List(keyCount) { color }

  fun colorfulPressedColors(
    keyCount: Int, tonic: Pitch = Pitch.C
  ): List<Color> {
    return List(keyCount) { keyNumber ->
      // no idea how I wrote this.
      val tonicOffset = keyNumber - tonic.ordinal
      val colorIndex = if (tonicOffset >= 0) tonicOffset else tonicOffset + 12
      Palette.entries[colorIndex].color
    }
  }

  fun disabledColors(keyCount: Int): List<Color> = List(keyCount) { index ->
    if (isBlackKey(index)) Color(0xFF3B3B3B) else Color(0xFFBDBDBD)
  }

  fun aspectRatio(keyCount: Int): Float {
    val whiteCount = (0 until keyCount).count { !isBlackKey(it) }
    return whiteCount / 4.2f
  }
}

@Composable
fun PianoKeyboard(
  modifier: Modifier = Modifier,

  keyCount: Int = 12,

  idleKeyColors: List<Color> = PianoKeyboardDefaults.idleColors(keyCount),
  pressedKeyColors: List<Color> = PianoKeyboardDefaults.pressedColors(keyCount),
  disabledKeyColors: List<Color> = PianoKeyboardDefaults.disabledColors(keyCount),
  enabledKeys: List<Boolean> = List(keyCount) { true },

  /**
   * External, declarative visual indications.
   *
   * PianoKeyIndication(index = 0, durationMillis = null) stays active until removed.
   * PianoKeyIndication(index = 0, durationMillis = 450) pulses for 450ms.
   */
  programmaticIndications: List<PianoKeyIndication> = emptyList(),

  onKeyClick: (index: Int) -> Unit = {},
  onKeyPressedChange: (index: Int, pressed: Boolean) -> Unit = { _, _ -> },

  borderColor: Color = Color(0xFF202124),
  separatorColor: Color = Color(0xFF202124),
  disabledOverlayColor: Color = Color.Black.copy(alpha = 0.16f),

  borderWidth: Dp = 4.dp,
  separatorWidth: Dp = 3.dp,

  blackKeyWidthFraction: Float = 0.52f,
  blackKeyHeightFraction: Float = 0.62f,

  whiteKeyCornerRadius: Dp = 0.dp,
  blackKeyCornerRadius: Dp = 2.dp,
) {
  require(keyCount > 0) {
    "keyCount must be greater than 0."
  }
  require(idleKeyColors.size == keyCount) {
    "idleKeyColors must have size keyCount."
  }
  require(pressedKeyColors.size == keyCount) {
    "pressedKeyColors must have size keyCount."
  }
  require(disabledKeyColors.size == keyCount) {
    "disabledKeyColors must have size keyCount."
  }
  require(enabledKeys.size == keyCount) {
    "enabledKeys must have size keyCount."
  }
  require(blackKeyWidthFraction in 0.1f..0.95f) {
    "blackKeyWidthFraction should be between 0.1 and 0.95."
  }
  require(blackKeyHeightFraction in 0.1f..0.95f) {
    "blackKeyHeightFraction should be between 0.1 and 0.95."
  }

  val latestOnKeyClick by rememberUpdatedState(onKeyClick)
  val latestOnKeyPressedChange by rememberUpdatedState(onKeyPressedChange)

  val touchPointers = remember { mutableStateMapOf<PointerId, Int>() }
  val timedProgrammaticCounts = remember { mutableStateMapOf<Int, Int>() }

  val timedIndications = remember(programmaticIndications) {
    programmaticIndications.filter { it.durationMillis != null }
  }

  LaunchedEffect(timedIndications, keyCount, enabledKeys) {
    timedIndications.forEach { indication ->
      val index = indication.index
      val duration = indication.durationMillis ?: return@forEach

      if (index !in 0 until keyCount) return@forEach
      if (!enabledKeys[index]) return@forEach

      launch {
        timedProgrammaticCounts[index] = (timedProgrammaticCounts[index] ?: 0) + 1

        try {
          delay(duration.coerceAtLeast(1L))
        } finally {
          val next = (timedProgrammaticCounts[index] ?: 1) - 1

          if (next <= 0) {
            timedProgrammaticCounts.remove(index)
          } else {
            timedProgrammaticCounts[index] = next
          }
        }
      }
    }
  }

  val persistentProgrammaticKeys =
    programmaticIndications.asSequence().filter { it.durationMillis == null }.map { it.index }
      .filter { it in 0 until keyCount && enabledKeys[it] }.toSet()

  val activeKeys =
    (touchPointers.values.toSet() + persistentProgrammaticKeys + timedProgrammaticCounts.keys).filter { it in 0 until keyCount && enabledKeys[it] }
      .toSet()

  val pressProgress = List(keyCount) { index ->
    animateFloatAsState(
      targetValue = if (index in activeKeys) 1f else 0f,
      animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow,
      ),
      label = "piano-key-press-$index",
    ).value
  }

  val infiniteTransition = rememberInfiniteTransition(label = "piano-key-pulse")

  val pulse by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(
        durationMillis = 850,
        easing = LinearEasing,
      ),
      repeatMode = RepeatMode.Restart,
    ),
    label = "piano-key-pulse-value",
  )

  Canvas(
    modifier = modifier.pointerInput(
      keyCount,
      enabledKeys,
      borderWidth,
      blackKeyWidthFraction,
      blackKeyHeightFraction,
    ) {
      fun currentGeometry(): List<PianoKeyGeometry> = buildPianoKeyGeometry(
        width = size.width.toFloat(),
        height = size.height.toFloat(),
        keyCount = keyCount,
        borderPx = borderWidth.toPx(),
        blackKeyWidthFraction = blackKeyWidthFraction,
        blackKeyHeightFraction = blackKeyHeightFraction,
      )

      fun hitTest(position: Offset): Int? {
        val geometry = currentGeometry()

        val blackHit =
          geometry.asSequence().filter { it.isBlack }.firstOrNull { it.rect.contains(position) }

        if (blackHit != null) {
          return if (enabledKeys[blackHit.index]) {
            blackHit.index
          } else {
            null
          }
        }

        val whiteHit =
          geometry.asSequence().filter { !it.isBlack }.firstOrNull { it.rect.contains(position) }

        return if (whiteHit != null && enabledKeys[whiteHit.index]) {
          whiteHit.index
        } else {
          null
        }
      }

      fun emitReleaseIfNeeded(keyIndex: Int) {
        if (!touchPointers.containsValue(keyIndex)) {
          latestOnKeyPressedChange(keyIndex, false)
        }
      }

      fun setPointerKey(
        pointerId: PointerId,
        newKey: Int?,
      ): Boolean {
        val oldKey = touchPointers[pointerId]

        if (oldKey == newKey) {
          return newKey != null
        }

        if (oldKey != null) {
          touchPointers.remove(pointerId)
          emitReleaseIfNeeded(oldKey)
        }

        if (newKey != null) {
          val wasAlreadyPressed = touchPointers.containsValue(newKey)

          touchPointers[pointerId] = newKey
          latestOnKeyClick(newKey)

          if (!wasAlreadyPressed) {
            latestOnKeyPressedChange(newKey, true)
          }
        }

        return oldKey != null || newKey != null
      }

      fun releasePointer(pointerId: PointerId): Boolean {
        val oldKey = touchPointers.remove(pointerId) ?: return false
        emitReleaseIfNeeded(oldKey)
        return true
      }

      fun releaseAll() {
        val releasedKeys = touchPointers.values.toSet()
        touchPointers.clear()
        releasedKeys.forEach { latestOnKeyPressedChange(it, false) }
      }

      try {
        awaitEachGesture {
          try {
            val firstDown =
              awaitPointerEvent().changes.firstOrNull { it.pressed && !it.previousPressed }
                ?: return@awaitEachGesture

            if (setPointerKey(firstDown.id, hitTest(firstDown.position))) {
              firstDown.consume()
            }

            while (true) {
              val event = awaitPointerEvent()

              event.changes.forEach { change ->
                val consumed = when {
                  change.pressed && !change.previousPressed -> {
                    setPointerKey(
                      pointerId = change.id,
                      newKey = hitTest(change.position),
                    )
                  }

                  change.pressed && change.previousPressed -> {
                    setPointerKey(
                      pointerId = change.id,
                      newKey = hitTest(change.position),
                    )
                  }

                  !change.pressed && change.previousPressed -> {
                    releasePointer(change.id)
                  }

                  else -> false
                }

                if (consumed) {
                  change.consume()
                }
              }

              if (event.changes.none { it.pressed }) {
                break
              }
            }
          } finally {
            releaseAll()
          }
        }
      } finally {
        releaseAll()
      }
    },
  ) {
    val geometry = buildPianoKeyGeometry(
      width = size.width,
      height = size.height,
      keyCount = keyCount,
      borderPx = borderWidth.toPx(),
      blackKeyWidthFraction = blackKeyWidthFraction,
      blackKeyHeightFraction = blackKeyHeightFraction,
    )

    val whiteCorner = CornerRadius(
      whiteKeyCornerRadius.toPx(),
      whiteKeyCornerRadius.toPx(),
    )

    val blackCorner = CornerRadius(
      blackKeyCornerRadius.toPx(),
      blackKeyCornerRadius.toPx(),
    )

    val separatorWidthPx = separatorWidth.toPx()
    val borderWidthPx = borderWidth.toPx()

    // White keys first.
    geometry.filter { !it.isBlack }.forEach { key ->
      val index = key.index
      val progress = pressProgress[index]
      val enabled = enabledKeys[index]
      val rect = key.rect

      val baseColor = when {
        !enabled -> disabledKeyColors[index]
        else -> lerp(
          idleKeyColors[index],
          pressedKeyColors[index],
          progress,
        )
      }

      drawRoundRect(
        color = baseColor,
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = whiteCorner,
      )

      if (enabled && progress > 0.01f) {
        val activeColor = pressedKeyColors[index]
        val pulseAlpha = 0.10f + 0.10f * pulse

        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.White.copy(alpha = 0.22f * progress),
              activeColor.copy(alpha = pulseAlpha * progress),
              Color.Transparent,
            ),
            startY = rect.top,
            endY = rect.bottom,
          ),
          topLeft = rect.topLeft,
          size = rect.size,
          cornerRadius = whiteCorner,
        )

//          drawRoundRect(
//            color = activeColor.copy(alpha = 0.55f * progress),
//            topLeft = rect.topLeft,
//            size = rect.size,
//            cornerRadius = whiteCorner,
//            style = Stroke(width = 2.dp.toPx()),
//          )
      }

      if (!enabled) {
        drawRoundRect(
          color = disabledOverlayColor,
          topLeft = rect.topLeft,
          size = rect.size,
          cornerRadius = whiteCorner,
        )
      }
    }

    // White separators.
    geometry.filter { !it.isBlack }.drop(1).forEach { key ->
      drawLine(
        color = separatorColor,
        start = Offset(key.rect.left, key.rect.top),
        end = Offset(key.rect.left, key.rect.bottom),
        strokeWidth = separatorWidthPx,
      )
    }

    // Black key shadows.
    geometry.filter { it.isBlack }.forEach { key ->
      val rect = key.rect

      drawRoundRect(
        color = Color.Black.copy(alpha = 0.30f),
        topLeft = Offset(
          x = rect.left - 2.dp.toPx(),
          y = rect.top,
        ),
        size = Size(
          width = rect.width + 4.dp.toPx(),
          height = rect.height + 4.dp.toPx(),
        ),
        cornerRadius = blackCorner,
      )
    }

    // Black keys over white keys.
    geometry.filter { it.isBlack }.forEach { key ->
      val index = key.index
      val progress = pressProgress[index]
      val enabled = enabledKeys[index]
      val rect = key.rect

      val baseColor = when {
        !enabled -> disabledKeyColors[index]
        else -> lerp(
          idleKeyColors[index],
          pressedKeyColors[index],
          progress,
        )
      }

      drawRoundRect(
        color = baseColor,
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = blackCorner,
      )

      if (enabled && progress > 0.01f) {
        val activeColor = pressedKeyColors[index]
        val pulseAlpha = 0.16f + 0.14f * pulse

        drawRoundRect(
          brush = Brush.verticalGradient(
            colors = listOf(
              Color.White.copy(alpha = 0.18f * progress),
              activeColor.copy(alpha = pulseAlpha * progress),
              Color.Transparent,
            ),
            startY = rect.top,
            endY = rect.bottom,
          ),
          topLeft = rect.topLeft,
          size = rect.size,
          cornerRadius = blackCorner,
        )

        drawRoundRect(
          color = activeColor.copy(alpha = 0.75f * progress),
          topLeft = rect.topLeft,
          size = rect.size,
          cornerRadius = blackCorner,
          style = Stroke(width = 2.dp.toPx()),
        )
      }

      if (!enabled) {
        drawRoundRect(
          color = disabledOverlayColor,
          topLeft = rect.topLeft,
          size = rect.size,
          cornerRadius = blackCorner,
        )
      }

      drawRoundRect(
        color = separatorColor,
        topLeft = rect.topLeft,
        size = rect.size,
        cornerRadius = blackCorner,
        style = Stroke(width = 2.dp.toPx()),
      )
    }

    // Outer border.
    drawRect(
      color = borderColor,
      topLeft = Offset.Zero,
      size = size,
      style = Stroke(width = borderWidthPx),
    )
  }
}

private data class PianoKeyGeometry(
  val index: Int,
  val isBlack: Boolean,
  val rect: Rect,
)

private fun buildPianoKeyGeometry(
  width: Float,
  height: Float,
  keyCount: Int,
  borderPx: Float,
  blackKeyWidthFraction: Float,
  blackKeyHeightFraction: Float,
): List<PianoKeyGeometry> {
  val safeWidth = width.coerceAtLeast(1f)
  val safeHeight = height.coerceAtLeast(1f)

  val contentLeft = borderPx
  val contentTop = borderPx
  val contentRight = safeWidth - borderPx
  val contentBottom = safeHeight - borderPx

  val contentWidth = (contentRight - contentLeft).coerceAtLeast(1f)
  val contentHeight = (contentBottom - contentTop).coerceAtLeast(1f)

  val whiteCount =
    (0 until keyCount).count { !PianoKeyboardDefaults.isBlackKey(it) }.coerceAtLeast(1)

  val whiteKeyWidth = contentWidth / whiteCount
  val blackKeyWidth = whiteKeyWidth * blackKeyWidthFraction
  val blackKeyHeight = contentHeight * blackKeyHeightFraction

  val keys = mutableListOf<PianoKeyGeometry>()

  for (index in 0 until keyCount) {
    val isBlack = PianoKeyboardDefaults.isBlackKey(index)

    if (!isBlack) {
      val whiteOrdinal = countWhiteKeysBefore(index)

      val left = contentLeft + whiteOrdinal * whiteKeyWidth
      val right = contentLeft + (whiteOrdinal + 1) * whiteKeyWidth

      keys += PianoKeyGeometry(
        index = index,
        isBlack = false,
        rect = Rect(
          left = left,
          top = contentTop,
          right = right,
          bottom = contentBottom,
        ),
      )
    }
  }

  for (index in 0 until keyCount) {
    val isBlack = PianoKeyboardDefaults.isBlackKey(index)

    if (isBlack) {
      val whiteKeysBefore = countWhiteKeysBefore(index)
      val centerX = contentLeft + whiteKeysBefore * whiteKeyWidth

      val left = centerX - blackKeyWidth / 2f
      val right = centerX + blackKeyWidth / 2f

      keys += PianoKeyGeometry(
        index = index,
        isBlack = true,
        rect = Rect(
          left = left.coerceAtLeast(contentLeft),
          top = contentTop,
          right = right.coerceAtMost(contentRight),
          bottom = contentTop + blackKeyHeight,
        ),
      )
    }
  }

  return keys
}

private fun countWhiteKeysBefore(index: Int): Int =
  (0 until index).count { !PianoKeyboardDefaults.isBlackKey(it) }


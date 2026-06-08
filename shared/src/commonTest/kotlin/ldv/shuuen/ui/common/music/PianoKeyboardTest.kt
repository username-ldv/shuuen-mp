package ldv.shuuen.ui.common.music

import androidx.compose.ui.geometry.Offset
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class PianoKeyboardTest {
  @Test
  fun blackKeyHitboxesFillVisualGapAndChooseClosestKey() {
    val geometry = testGeometry()

    assertEquals(1, geometry.hitTest(Offset(x = 145f, y = 20f)))
    assertEquals(3, geometry.hitTest(Offset(x = 155f, y = 20f)))
  }

  @Test
  fun blackKeyHitboxesDoNotExtendBelowBlackKeys() {
    val geometry = testGeometry()

    assertEquals(2, geometry.hitTest(Offset(x = 145f, y = 80f)))
  }

  @Test
  fun blackKeyHitboxEndsHalfAWhiteKeyFromItsCenter() {
    val geometry = testGeometry()

    assertEquals(3, geometry.hitTest(Offset(x = 249f, y = 20f)))
    assertEquals(4, geometry.hitTest(Offset(x = 251f, y = 20f)))
  }

  @Test
  fun firstAndLastBlackKeyHitboxesUseTheSameHalfWidth() {
    val geometry = testGeometry()

    assertEquals(0, geometry.hitTest(Offset(x = 49f, y = 20f)))
    assertEquals(1, geometry.hitTest(Offset(x = 51f, y = 20f)))
    assertEquals(10, geometry.hitTest(Offset(x = 649f, y = 20f)))
    assertEquals(11, geometry.hitTest(Offset(x = 651f, y = 20f)))
  }

  @Test
  fun isolatedBlackKeyHitboxUsesTheSameHalfWidth() {
    val geometry = testGeometry()

    assertEquals(5, geometry.hitTest(Offset(x = 349f, y = 20f)))
    assertEquals(6, geometry.hitTest(Offset(x = 351f, y = 20f)))
    assertEquals(6, geometry.hitTest(Offset(x = 449f, y = 20f)))
    assertEquals(7, geometry.hitTest(Offset(x = 451f, y = 20f)))
  }

  private fun testGeometry(): List<PianoKeyGeometry> = buildPianoKeyGeometry(
    width = 700f,
    height = 100f,
    keyCount = 12,
    borderPx = 0f,
    blackKeyWidthFraction = 0.52f,
    blackKeyHeightFraction = 0.62f,
  )
}

private fun List<PianoKeyGeometry>.hitTest(position: Offset): Int? {
  val blackHit = asSequence()
    .filter { it.isBlack && it.hitRect.contains(position) }
    .minByOrNull { abs(position.x - it.rect.center.x) }

  if (blackHit != null) {
    return blackHit.index
  }

  return firstOrNull { !it.isBlack && it.rect.contains(position) }?.index
}

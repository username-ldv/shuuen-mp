package ldv.shuuen.bass

import kotlin.test.Test
import kotlin.test.assertTrue

class BassJvmSmokeTest {
  @Test
  fun loadsBassAndBassMidi() {
    Bass.load()

    assertTrue(Bass.version() != 0)
    assertTrue(Bass.midiVersion() != 0)
  }
}

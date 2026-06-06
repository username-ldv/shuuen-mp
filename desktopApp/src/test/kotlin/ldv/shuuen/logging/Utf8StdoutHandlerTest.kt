package ldv.shuuen.logging

import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.ZoneId
import java.util.logging.Level
import java.util.logging.LogRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Utf8StdoutHandlerTest {
  @Test
  fun `writes formatted record to UTF-8 output and flushes`() {
    val output = TrackingOutputStream()
    val handler = Utf8StdoutHandler(output, DesktopLogFormatter(ZoneId.of("UTC")))
    val record =
      LogRecord(Level.INFO, "[INFO] App - Привет").apply {
        instant = Instant.parse("2026-06-06T14:38:03.123Z")
      }

    handler.publish(record)

    assertEquals(
      "2026-06-06 14:38:03.123 [INFO] App - Привет${System.lineSeparator()}",
      output.toString(Charsets.UTF_8),
    )
    assertTrue(output.wasFlushed)
  }

  private class TrackingOutputStream : ByteArrayOutputStream() {
    var wasFlushed = false
      private set

    override fun flush() {
      super.flush()
      wasFlushed = true
    }
  }
}

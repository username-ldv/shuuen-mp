package ldv.shuuen.music

import ldv.shuuen.domain.audio.music.Chord
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.Timing
import kotlin.test.Test
import kotlin.test.assertEquals

class MusicDomainTest {
  @Test
  fun wrapsPitchArithmetic() {
    assertEquals(Pitch.B, Pitch.C - 1)
    assertEquals(Pitch.C, Pitch.B + 1)
    assertEquals(Pitch.D, Pitch.C + 14)
  }

  @Test
  fun mapsNotesToMidiAndBack() {
    assertEquals(60, Note(Pitch.C, 4).midiIndex)
    assertEquals(Pitch.A, Note(21).pitch)
    assertEquals(1, Note(Pitch.A, 0).pianoKeyNumber)
    assertEquals("C4", Note(Pitch.C, 4).name)
  }

  @Test
  fun mapsDegreesFromTonic() {
    assertEquals(Degree.D3, Note(Pitch.E, 4).degree(Pitch.C))
    assertEquals(Pitch.G, Degree.D5.pitch(Pitch.C))
  }

  @Test
  fun buildsNaturalMinorScale() {
    assertEquals(
      listOf(Pitch.C, Pitch.D, Pitch.DSharp, Pitch.F, Pitch.G, Pitch.GSharp, Pitch.ASharp, Pitch.C),
      Scale.naturalMinor(Pitch.C).pitches,
    )
  }

  @Test
  fun buildsMajorChord() {
    assertEquals(
      listOf(60, 64, 67),
      Chord.major(Note(Pitch.C, 4)).notes.map { it.midiIndex },
    )
  }

  @Test
  fun calculatesTimingValues() {
    assertEquals(500.0, Timing.quarter(120))
    assertEquals(250.0, Timing.eighth(120))
  }
}

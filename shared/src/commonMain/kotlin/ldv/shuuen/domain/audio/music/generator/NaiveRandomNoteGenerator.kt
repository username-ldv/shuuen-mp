package ldv.shuuen.domain.audio.music.generator

import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.NoteRange
import ldv.shuuen.domain.audio.music.Pitch

class NaiveRandomNoteGenerator(val range: NoteRange, val allowedPitches: List<Pitch>) :
  NoteGenerator {
  private val allowedNotes = (range.from..range.to).filter {
    allowedPitches.any { pitch -> pitch == it.pitch }
  }

  override fun next(): Note {
    return allowedNotes.random()
  }
}
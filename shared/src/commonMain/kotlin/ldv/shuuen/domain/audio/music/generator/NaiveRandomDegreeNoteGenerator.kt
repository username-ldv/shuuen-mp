package ldv.shuuen.domain.audio.music.generator

import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.NoteRange
import ldv.shuuen.domain.audio.music.Pitch

class NaiveRandomDegreeNoteGenerator(
  val root: Pitch, val range: NoteRange, val allowedDegrees: List<Degree>
) : NoteGenerator {
  private val allowedNotes = (range.from..range.to).filter {
    allowedDegrees.any { degree -> degree == root.asRoot(it.pitch) }
  }

  override fun next(): Note {
    return allowedNotes.random()
  }
}
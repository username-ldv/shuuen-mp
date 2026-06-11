package ldv.shuuen.domain.audio.music.generator

import ldv.shuuen.domain.audio.music.Note

interface NoteGenerator {
  fun next(): Note
}
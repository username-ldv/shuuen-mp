package ldv.shuuen.ui.screens.training.single.play

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.generator.NaiveRandomDegreeNoteGenerator
import ldv.shuuen.domain.audio.music.generator.NaiveRandomNoteGenerator
import ldv.shuuen.domain.audio.music.generator.NoteGenerator
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.singles.SinglesLevel

class SinglesLevelQuizzer(val level: SinglesLevel, midiEngine: MidiEngine) {
  private val _currentNote: MutableStateFlow<Note>
  private val _currentRoot: MutableStateFlow<Pitch>
  private val generator: NoteGenerator

  val currentNote: StateFlow<Note>
  val currentRoot: StateFlow<Pitch>

  init {
    when (val c = level.levelConfig) {
      is LevelConfig.Singles.Relative -> {
        val randomRoot = Pitch.random()
        _currentRoot = MutableStateFlow(randomRoot)

        generator = NaiveRandomDegreeNoteGenerator(
          root = randomRoot,
          range = level.range,
          allowedDegrees = c.scaleConfig.degreeStates.filter { it.active }.map { it.degree })
      }

      is LevelConfig.Singles.Absolute -> {
        _currentRoot = MutableStateFlow(c.scales.first().root)
        generator = NaiveRandomNoteGenerator(
          range = level.range,
          allowedPitches = c.scales.first().pitchStates.filter { it.active }.map { it.pitch })
      }
    }
    _currentNote = MutableStateFlow(generator.next())
    currentNote = _currentNote.asStateFlow()
    currentRoot = _currentRoot.asStateFlow()
  }

  fun next() {

  }
}
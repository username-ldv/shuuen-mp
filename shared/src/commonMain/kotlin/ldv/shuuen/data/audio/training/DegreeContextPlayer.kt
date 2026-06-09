package ldv.shuuen.data.audio.training

import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ldv.shuuen.domain.audio.engine.MidiEngine
import ldv.shuuen.domain.audio.midi.MidiChannel
import ldv.shuuen.domain.audio.music.Chord
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Sustain
import ldv.shuuen.domain.audio.music.chord
import ldv.shuuen.domain.audio.music.constructAscSetupMelodyFlow
import kotlin.time.Duration.Companion.milliseconds

private data class CurrentlyPlayingNode(
  val chord: Chord,
  val channel: MidiChannel,
  val startQuestionNumber: Int,
  val questionDuration: Int?
)

class DegreeContextPlayer(
  val midiEngine: MidiEngine,
  val context: DegreeContext,
  val root: Pitch,
  val endlessPreMelody: Double = 1500.0,
  val afterSetupMelody: Double = 1500.0
) {
  private val currentQuestion = MutableStateFlow(0)
  private val _ready = MutableStateFlow(false)
  val ready = _ready.asStateFlow()
  private var currentlyPlaying: CurrentlyPlayingNode? = null
  private var currentNodeCount = 0


  suspend fun start() {
    val c = context
    require(c.nodes.isNotEmpty()) { "context can't be empty" }

    Napier.v { "in start, context $context" }

    try {
      // should collect indefinitely
      currentQuestion.collect { questionNumber ->
        currentlyPlaying?.let { playing ->
          // if something plays already and the question duration is done, then stop
          if (playing.questionDuration != null && questionNumber - playing.startQuestionNumber >= playing.questionDuration) {
            stopCurrent(true)
            _ready.value = false
          } else {
            // else continue collecting current
            return@collect
          }
        }
        // if nothing plays, play next
        playNode(c, questionNumber)
      }
    } finally {
      Napier.v { "Finally happened" }
      // is it needed?
      _ready.value = false
      stopCurrent(advance = false)
    }
  }

  fun onQuestionChanged(questionNumber: Int) {
    currentQuestion.value = questionNumber
  }

  private fun stopCurrent(advance: Boolean) {
    val playing = currentlyPlaying ?: return
    midiEngine.stopChord(playing.chord, playing.channel)
    currentlyPlaying = null
    if (advance) currentNodeCount++
    Napier.v { "Stop current happened" }
  }

  private suspend fun playNode(c: DegreeContext, questionNumber: Int) {
    while (currentlyPlaying == null) {
      Napier.v { "While loop q: $questionNumber" }
      val node = c.nodes[currentNodeCount % c.nodes.size]
      val chord = node.degrees.map { d -> Note(d.degree.pitch(root), d.octave) }.chord()
      val channel = when (node.sustain) {
        is Sustain.Endless -> MidiChannel.Drone
        is Sustain.Finite -> MidiChannel.Cadence
      }
      midiEngine.playChord(chord, channel)
      currentlyPlaying =
        CurrentlyPlayingNode(chord, channel, questionNumber, node.durationInQuestions)

      when (val sustain = node.sustain) {
        is Sustain.Endless -> {
          delay(endlessPreMelody.milliseconds)
          if (c.setupMelody != null) {
            var currentlyPlaying: Note? = null
            constructAscSetupMelodyFlow(root, c.setupMelody).collect { note ->
              currentlyPlaying?.let { midiEngine.stopNote(it) }
              midiEngine.playNote(note, MidiChannel.Notes)
              currentlyPlaying = note
            }
            currentlyPlaying?.let { midiEngine.stopNote(it) }
            delay(afterSetupMelody.milliseconds)
          }
          _ready.value = true
          return
        }

        is Sustain.Finite -> {
          delay(sustain.duration.milliseconds)
          stopCurrent(true)
          // if durationInQuestion = 0 then just play next immediately
          if (node.durationInQuestions == 0) continue
          _ready.value = true
          return
        }
      }
    }
  }
}
package ldv.shuuen.domain.audio.midi

enum class MidiChannel(val id: Int) {
  Notes(0),
  Drone(1),
  Cadence(2),
}
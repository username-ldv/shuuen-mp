package ldv.shuuen.singles

import ldv.shuuen.music.Pitch
import ldv.shuuen.music.Scale

enum class SinglesDisplayMode {
  Degrees,
  Notes,
}

data class SinglesState(
  val tonic: Pitch = Pitch.C,
  val enabledKeyboardKeys: List<Boolean> = List(12) { true },
  val activeKeyboardKeys: Set<Int> = emptySet(),
  val activeFifthsItems: Set<Int> = emptySet(),
  val displayMode: SinglesDisplayMode = SinglesDisplayMode.Degrees,
  val audioReady: Boolean = false,
  val initializingAudio: Boolean = true,
  val errorMessage: String? = null,
) {
  companion object {
    fun initial(tonic: Pitch = Pitch.C): SinglesState {
      val enabledPitches = Scale.naturalMinor(tonic).pitches.map { it.ordinal }.toSet()
      return SinglesState(
        tonic = tonic,
        enabledKeyboardKeys = List(12) { it in enabledPitches },
      )
    }
  }
}

sealed interface SinglesAction {
  data class PressPitch(val pitchIndex: Int) : SinglesAction
  data class ReleasePitch(val pitchIndex: Int) : SinglesAction
  data class ToggleDrone(val fifthsIndex: Int) : SinglesAction
  data object StopAll : SinglesAction
  data object DismissError : SinglesAction
}

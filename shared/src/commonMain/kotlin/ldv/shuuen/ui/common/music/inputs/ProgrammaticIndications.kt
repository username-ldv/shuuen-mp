package ldv.shuuen.ui.common.music.inputs

sealed interface ProgrammaticIndications {
  val index: Int
  /**
   * null = persistent while this object is present in programmaticIndications.
   * non-null = animate for this many milliseconds.
   */
  val durationMillis: Long?


}
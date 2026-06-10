package ldv.shuuen.domain.training.level

enum class LevelSource(val dbValue: String) {
  BuiltIn("built_in"),
  User("user"),
  Imported("imported"),
}
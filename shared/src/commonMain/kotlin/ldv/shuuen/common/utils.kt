package ldv.shuuen.common

inline fun <T, K> List<T>.upsertBy(
  item: T,
  key: (T) -> K,
): List<T> {
  val index = indexOfFirst { key(it) == key(item) }

  return if (index == -1) {
    this + item
  } else {
    toMutableList().also { it[index] = item }
  }
}

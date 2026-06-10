package ldv.shuuen.domain.training.context

import kotlinx.serialization.Serializable

@Serializable
sealed interface ContextConfig {
  data class Degree(val name: String?) : ContextConfig
}
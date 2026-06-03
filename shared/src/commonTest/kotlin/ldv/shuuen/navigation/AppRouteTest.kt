package ldv.shuuen.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppRouteTest {
  @Test
  fun includesMelodiesTrainingRoutes() {
    val routes: List<AppRoute> = listOf(
      AppRoute.MelodiesSetup,
      AppRoute.MelodiesPlay,
      AppRoute.Context,
    )

    assertEquals(AppRoute.MelodiesSetup, routes.first())
    assertEquals(AppRoute.Context, routes.last())
    assertTrue(routes.toSet().size == routes.size)
  }
}

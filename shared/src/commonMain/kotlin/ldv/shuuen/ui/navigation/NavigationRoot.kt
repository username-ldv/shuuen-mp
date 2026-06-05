package ldv.shuuen.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

data class Transitions(
  val transitionSpec: AnimatedContentTransitionScope<Scene<AppRoute>>.() -> ContentTransform,
  val popTransitionSpec: AnimatedContentTransitionScope<Scene<AppRoute>>.() -> ContentTransform,
  val predictivePopTransitionSpec: AnimatedContentTransitionScope<Scene<AppRoute>>.(Int) -> ContentTransform
)

expect val transitions: Transitions

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationRoot() {
  val entryProvider = koinEntryProvider<AppRoute>()
  val navigator = koinInject<AppNavigator>()

  NavDisplay(
    entryProvider = entryProvider,
    backStack = navigator.backStack,
    onBack = { navigator.navigateBack() },
    entryDecorators = listOf(
      rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator()
    ),
    transitionSpec = transitions.transitionSpec,
    popTransitionSpec = transitions.popTransitionSpec,
    predictivePopTransitionSpec = transitions.predictivePopTransitionSpec,
  )
}
package fr.superlamp.mobile.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.superlamp.mobile.ui.exercises.ExerciseDetailScreen
import fr.superlamp.mobile.ui.exercises.ExerciseListScreen
import fr.superlamp.mobile.ui.history.HistoryScreen
import fr.superlamp.mobile.ui.home.HomeScreen
import fr.superlamp.mobile.ui.importtext.ImportScreen
import fr.superlamp.mobile.ui.lift.LiftScreen
import fr.superlamp.mobile.ui.programs.SplitDetailScreen
import fr.superlamp.mobile.ui.programs.SplitListScreen
import fr.superlamp.mobile.ui.programs.WorkoutDetailScreen

object Routes {
    const val HOME = "home"
    const val PROGRAMS = "programs"
    const val EXERCISES = "exercises"
    const val HISTORY = "history"
    const val IMPORT = "import"
    const val SPLIT = "split/{splitId}"
    const val WORKOUT = "workout/{workoutId}"
    const val LIFT = "lift/{liftId}"
    const val EXERCISE = "exercise/{exerciseId}"

    fun split(id: Long) = "split/$id"
    fun workout(id: Long) = "workout/$id"
    fun lift(id: Long) = "lift/$id"
    fun exercise(id: Long) = "exercise/$id"
}

private enum class TopLevelDestination(val route: String, val label: String, val icon: ImageVector) {
    HOME(Routes.HOME, "Accueil", Icons.Default.Home),
    PROGRAMS(Routes.PROGRAMS, "Programmes", Icons.Default.CalendarMonth),
    EXERCISES(Routes.EXERCISES, "Exercices", Icons.Default.FitnessCenter),
    HISTORY(Routes.HISTORY, "Historique", Icons.Default.History),
}

@Composable
fun SuperLampNavHost() {
    val navController = rememberNavController()
    val bottomBar: @Composable () -> Unit = { MainBottomBar(navController) }
    val openLift: (Long) -> Unit = { navController.navigate(Routes.lift(it)) }
    val back: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                bottomBar = bottomBar,
                onOpenLift = openLift,
                onOpenPrograms = { navController.navigateTopLevel(Routes.PROGRAMS) },
                onImport = { navController.navigate(Routes.IMPORT) },
            )
        }
        composable(Routes.PROGRAMS) {
            SplitListScreen(
                bottomBar = bottomBar,
                onOpenSplit = { navController.navigate(Routes.split(it)) },
            )
        }
        composable(Routes.EXERCISES) {
            ExerciseListScreen(
                bottomBar = bottomBar,
                onOpenExercise = { navController.navigate(Routes.exercise(it)) },
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                bottomBar = bottomBar,
                onOpenLift = openLift,
                onImport = { navController.navigate(Routes.IMPORT) },
            )
        }
        composable(Routes.SPLIT, arguments = listOf(navArgument("splitId") { type = NavType.LongType })) {
            SplitDetailScreen(
                onBack = back,
                onOpenWorkout = { navController.navigate(Routes.workout(it)) },
            )
        }
        composable(Routes.WORKOUT, arguments = listOf(navArgument("workoutId") { type = NavType.LongType })) {
            WorkoutDetailScreen(onBack = back, onLiftStarted = openLift)
        }
        composable(Routes.LIFT, arguments = listOf(navArgument("liftId") { type = NavType.LongType })) {
            LiftScreen(onBack = back)
        }
        composable(Routes.EXERCISE, arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })) {
            ExerciseDetailScreen(onBack = back, onOpenLift = openLift)
        }
        composable(Routes.IMPORT) {
            ImportScreen(
                onBack = back,
                onSaved = { liftId ->
                    navController.navigate(Routes.lift(liftId)) {
                        popUpTo(Routes.IMPORT) { inclusive = true }
                    }
                },
            )
        }
    }
}

private fun NavHostController.navigateTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun MainBottomBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = { navController.navigateTopLevel(destination.route) },
                icon = { Icon(destination.icon, contentDescription = null) },
                label = { Text(destination.label) },
            )
        }
    }
}

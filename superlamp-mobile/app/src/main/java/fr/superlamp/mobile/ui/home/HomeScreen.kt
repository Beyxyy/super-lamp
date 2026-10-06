@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSummary
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.entity.WorkoutWithSplit
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.LiftSummaryCard
import fr.superlamp.mobile.ui.components.StatCard
import fr.superlamp.mobile.ui.plural
import fr.superlamp.mobile.ui.shortDay
import fr.superlamp.mobile.ui.theme.Corinthia
import fr.superlamp.mobile.ui.time
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate

data class HomeUiState(
    val activeLift: Lift? = null,
    val nextWorkout: WorkoutWithSplit? = null,
    val recent: List<LiftSummary> = emptyList(),
    val weekCount: Int = 0,
    val totalCount: Int = 0,
    val loaded: Boolean = false,
)

class HomeViewModel(
    programRepository: ProgramRepository,
    private val liftRepository: LiftRepository,
) : BaseViewModel() {

    val state: StateFlow<HomeUiState> = combine(
        liftRepository.observeActive(),
        programRepository.observeNextWorkout(),
        liftRepository.observeSummaries(),
    ) { active, next, lifts ->
        val weekStart = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay()
        HomeUiState(
            activeLift = active,
            nextWorkout = next,
            recent = lifts.filter { it.lift.id != active?.id }.take(3),
            weekCount = lifts.count { it.lift.startTime >= weekStart },
            totalCount = lifts.size,
            loaded = true,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun startLift(workout: Workout?, onStarted: (Long) -> Unit) = launchCatching {
        onStarted(liftRepository.startLift(workout))
    }

    companion object {
        val Factory = appViewModelFactory { HomeViewModel(it.programRepository, it.liftRepository) }
    }
}

@Composable
fun HomeScreen(
    bottomBar: @Composable () -> Unit,
    onOpenLift: (Long) -> Unit,
    onOpenPrograms: () -> Unit,
    onImport: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SuperLamp",
                        fontFamily = Corinthia,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
            )
        },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            state.activeLift?.let { lift ->
                item(key = "active") {
                    ActiveLiftCard(lift, onResume = { onOpenLift(lift.id) })
                }
            }
            item(key = "next") {
                NextWorkoutCard(
                    next = state.nextWorkout,
                    loaded = state.loaded,
                    onStart = { workout -> viewModel.startLift(workout, onOpenLift) },
                    onOpenPrograms = onOpenPrograms,
                )
            }
            item(key = "actions") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { viewModel.startLift(null, onOpenLift) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Séance libre")
                    }
                    OutlinedButton(onClick = onImport, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(8.dp))
                        Text("Importer")
                    }
                }
            }
            item(key = "stats") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Cette semaine", plural(state.weekCount, "séance"), Modifier.weight(1f))
                    StatCard("Au total", plural(state.totalCount, "séance"), Modifier.weight(1f))
                }
            }
            if (state.recent.isNotEmpty()) {
                item(key = "recent-title") {
                    Text("Dernières séances", style = MaterialTheme.typography.titleMedium)
                }
                items(state.recent, key = { it.lift.id }) { summary ->
                    LiftSummaryCard(summary, onClick = { onOpenLift(summary.lift.id) })
                }
            }
        }
    }
}

@Composable
private fun ActiveLiftCard(lift: Lift, onResume: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp)) {
            Column(Modifier.weight(1f)) {
                Text("Séance en cours", style = MaterialTheme.typography.labelLarge)
                Text(lift.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "Commencée ${lift.startTime.shortDay().lowercase()} à ${lift.startTime.time()}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            FilledTonalButton(onClick = onResume) { Text("Reprendre") }
        }
    }
}

/** Bouton vers la prochaine séance prévue (PLAN.md, CU-01 / CU-02). */
@Composable
private fun NextWorkoutCard(
    next: WorkoutWithSplit?,
    loaded: Boolean,
    onStart: (Workout) -> Unit,
    onOpenPrograms: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Prochaine séance", style = MaterialTheme.typography.labelLarge)
            if (next != null) {
                Text(next.workout.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(next.splitName, style = MaterialTheme.typography.bodyMedium)
                next.workout.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                Spacer(Modifier.size(4.dp))
                Button(
                    onClick = { onStart(next.workout) },
                    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Démarrer")
                }
            } else if (loaded) {
                Text("Aucun programme pour l'instant", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Crée un programme (ex. Push / Pull / Legs) et ses séances : l'app te proposera automatiquement la suivante.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(Modifier.size(4.dp))
                Button(onClick = onOpenPrograms) { Text("Créer un programme") }
            }
        }
    }
}

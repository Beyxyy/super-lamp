@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.exercises

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.ExerciseSetHistory
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.data.repository.ExerciseRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.BackButton
import fr.superlamp.mobile.ui.components.ConfirmDialog
import fr.superlamp.mobile.ui.components.EmptyState
import fr.superlamp.mobile.ui.components.ExerciseFormDialog
import fr.superlamp.mobile.ui.components.ProgressChart
import fr.superlamp.mobile.ui.components.StatCard
import fr.superlamp.mobile.ui.formatSetsCompact
import fr.superlamp.mobile.ui.formatWeight
import fr.superlamp.mobile.ui.shortDay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDateTime

/** Les séries d'un exercice lors d'une séance. */
data class ExerciseSession(
    val liftId: Long,
    val date: LocalDateTime,
    val liftName: String,
    val sets: List<LiftSet>,
) {
    val bestWeight: Double? = sets.mapNotNull { it.weightKg }.maxOrNull()

    /** 1RM estimé (formule d'Epley) sur la meilleure série. */
    val bestOneRepMax: Double? = sets.mapNotNull { set ->
        set.weightKg?.let { weight -> if (set.reps == 1) weight else weight * (1 + set.reps / 30.0) }
    }.maxOrNull()
}

data class ExerciseDetailState(
    val exercise: Exercise? = null,
    val sessions: List<ExerciseSession> = emptyList(),
    val loaded: Boolean = false,
) {
    val record: Double? get() = sessions.mapNotNull { it.bestWeight }.maxOrNull()
    val bestOneRepMax: Double? get() = sessions.mapNotNull { it.bestOneRepMax }.maxOrNull()

    /** Charge max par séance, dans l'ordre chronologique. */
    val chartValues: List<Double> get() = sessions.reversed().mapNotNull { it.bestWeight }
}

class ExerciseDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ExerciseRepository,
) : BaseViewModel() {
    private val exerciseId: Long = checkNotNull(savedStateHandle.get<Long>("exerciseId"))

    val state: StateFlow<ExerciseDetailState> = combine(
        repository.observeById(exerciseId),
        repository.observeHistory(exerciseId),
    ) { exercise, history -> ExerciseDetailState(exercise, history.toSessions(), loaded = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExerciseDetailState())

    fun update(name: String, muscleGroup: String, description: String) = launchCatching {
        val exercise = state.value.exercise ?: return@launchCatching
        repository.update(exercise, name, muscleGroup, description)
    }

    fun delete(onDeleted: () -> Unit) = launchCatching {
        val exercise = state.value.exercise ?: return@launchCatching
        repository.delete(exercise)
        onDeleted()
    }

    companion object {
        val Factory = appViewModelFactory { ExerciseDetailViewModel(createSavedStateHandle(), it.exerciseRepository) }
    }
}

/** Regroupe les séries par séance (l'historique arrive trié de la plus récente à la plus ancienne). */
private fun List<ExerciseSetHistory>.toSessions(): List<ExerciseSession> =
    groupBy { it.set.liftId }.map { (liftId, rows) ->
        ExerciseSession(
            liftId = liftId,
            date = rows.first().liftStartTime,
            liftName = rows.first().liftName,
            sets = rows.map { it.set },
        )
    }

@Composable
fun ExerciseDetailScreen(
    onBack: () -> Unit,
    onOpenLift: (Long) -> Unit,
    viewModel: ExerciseDetailViewModel = viewModel(factory = ExerciseDetailViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    val exercise = state.exercise

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name.orEmpty()) },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    if (exercise != null) {
                        IconButton(onClick = { showEdit = true }) { Icon(Icons.Default.Edit, contentDescription = "Modifier") }
                        IconButton(onClick = { showDelete = true }) { Icon(Icons.Default.Delete, contentDescription = "Supprimer") }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (exercise != null && (exercise.muscleGroup != null || exercise.description != null)) {
                item(key = "info") {
                    Column {
                        exercise.muscleGroup?.let {
                            Text(it, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        }
                        exercise.description?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item(key = "stats") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Record", formatWeight(state.record), Modifier.weight(1f))
                    StatCard("1RM estimé", formatWeight(state.bestOneRepMax?.let { Math.round(it * 2) / 2.0 }), Modifier.weight(1f))
                    StatCard("Séances", state.sessions.size.toString(), Modifier.weight(1f))
                }
            }
            val chartValues = state.chartValues
            if (chartValues.size >= 2) {
                item(key = "chart") {
                    OutlinedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Progression (charge max par séance)", style = MaterialTheme.typography.titleSmall)
                            ProgressChart(
                                values = chartValues,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                            )
                            Row {
                                Text(
                                    "min ${formatWeight(chartValues.min())}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f),
                                )
                                Text(
                                    "max ${formatWeight(chartValues.max())}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
            if (state.loaded && state.sessions.isEmpty()) {
                item(key = "empty") {
                    EmptyState(
                        icon = Icons.Default.History,
                        title = "Pas encore d'historique",
                        message = "Les séries que tu enregistres pour cet exercice apparaîtront ici.",
                    )
                }
            } else if (state.sessions.isNotEmpty()) {
                item(key = "history-title") {
                    Text("Historique", style = MaterialTheme.typography.titleMedium)
                }
                items(state.sessions, key = { it.liftId }) { session ->
                    ElevatedCard(onClick = { onOpenLift(session.liftId) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                "${session.date.shortDay()} · ${session.liftName}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Text(formatSetsCompact(session.sets), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    if (showEdit && exercise != null) {
        ExerciseFormDialog(
            title = "Modifier l'exercice",
            initial = exercise,
            onDismiss = { showEdit = false },
            onConfirm = { name, group, description ->
                showEdit = false
                viewModel.update(name, group, description)
            },
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Supprimer l'exercice ?",
            message = "Il sera retiré des programmes et toutes ses séries seront supprimées de l'historique.",
            onDismiss = { showDelete = false },
            onConfirm = { viewModel.delete(onBack) },
        )
    }
}

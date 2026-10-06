@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Split
import fr.superlamp.mobile.data.entity.Workout
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.BackButton
import fr.superlamp.mobile.ui.components.ConfirmDialog
import fr.superlamp.mobile.ui.components.EmptyState
import fr.superlamp.mobile.ui.components.NameDescriptionDialog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SplitDetailState(
    val split: Split? = null,
    val workouts: List<Workout> = emptyList(),
    val loaded: Boolean = false,
)

class SplitDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: ProgramRepository,
) : BaseViewModel() {
    private val splitId: Long = checkNotNull(savedStateHandle.get<Long>("splitId"))

    val state: StateFlow<SplitDetailState> = combine(
        repository.observeSplit(splitId),
        repository.observeWorkouts(splitId),
    ) { split, workouts -> SplitDetailState(split, workouts, loaded = true) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SplitDetailState())

    fun update(name: String, description: String) = launchCatching {
        val split = state.value.split ?: return@launchCatching
        repository.updateSplit(split, name, description)
    }

    fun delete(onDeleted: () -> Unit) = launchCatching {
        val split = state.value.split ?: return@launchCatching
        repository.deleteSplit(split)
        onDeleted()
    }

    fun createWorkout(name: String, description: String) = launchCatching {
        repository.createWorkout(splitId, name, description)
    }

    fun move(workout: Workout, direction: Int) = launchCatching {
        repository.moveWorkout(workout, direction)
    }

    companion object {
        val Factory = appViewModelFactory { SplitDetailViewModel(createSavedStateHandle(), it.programRepository) }
    }
}

@Composable
fun SplitDetailScreen(
    onBack: () -> Unit,
    onOpenWorkout: (Long) -> Unit,
    viewModel: SplitDetailViewModel = viewModel(factory = SplitDetailViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    var showCreate by rememberSaveable { mutableStateOf(false) }
    var showEdit by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    val split = state.split

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(split?.name.orEmpty()) },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    if (split != null) {
                        IconButton(onClick = { showEdit = true }) { Icon(Icons.Default.Edit, contentDescription = "Modifier") }
                        IconButton(onClick = { showDelete = true }) { Icon(Icons.Default.Delete, contentDescription = "Supprimer") }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Séance") },
            )
        },
    ) { padding ->
        if (state.loaded && state.workouts.isEmpty()) {
            EmptyState(
                icon = Icons.Default.FitnessCenter,
                title = "Aucune séance type",
                message = "Ajoute les séances de ce programme dans l'ordre où tu les enchaînes (ex. Push, Pull, Legs).",
                modifier = Modifier.padding(padding),
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                split?.description?.let { description ->
                    item(key = "description") {
                        Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                itemsIndexed(state.workouts, key = { _, workout -> workout.id }) { index, workout ->
                    WorkoutRow(
                        position = index + 1,
                        workout = workout,
                        canMoveUp = index > 0,
                        canMoveDown = index < state.workouts.lastIndex,
                        onClick = { onOpenWorkout(workout.id) },
                        onMove = { direction -> viewModel.move(workout, direction) },
                    )
                }
            }
        }
    }

    if (showCreate) {
        NameDescriptionDialog(
            title = "Nouvelle séance type",
            nameLabel = "Nom (ex. Push)",
            onDismiss = { showCreate = false },
            onConfirm = { name, description ->
                showCreate = false
                viewModel.createWorkout(name, description)
            },
        )
    }
    if (showEdit && split != null) {
        NameDescriptionDialog(
            title = "Modifier le programme",
            initialName = split.name,
            initialDescription = split.description.orEmpty(),
            onDismiss = { showEdit = false },
            onConfirm = { name, description ->
                showEdit = false
                viewModel.update(name, description)
            },
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Supprimer le programme ?",
            message = "Ses séances types seront supprimées. Ton historique de séances est conservé.",
            onDismiss = { showDelete = false },
            onConfirm = { viewModel.delete(onBack) },
        )
    }
}

@Composable
private fun WorkoutRow(
    position: Int,
    workout: Workout,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onClick: () -> Unit,
    onMove: (Int) -> Unit,
) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text("$position", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Spacer(Modifier.size(16.dp))
            Column(Modifier.weight(1f)) {
                Text(workout.name, style = MaterialTheme.typography.titleMedium)
                workout.description?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = { onMove(-1) }, enabled = canMoveUp) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Monter")
            }
            IconButton(onClick = { onMove(1) }, enabled = canMoveDown) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Descendre")
            }
        }
    }
}

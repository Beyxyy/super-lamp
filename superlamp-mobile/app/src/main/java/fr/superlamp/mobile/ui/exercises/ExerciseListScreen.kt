@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.repository.ExerciseRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.ExerciseFormDialog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ExerciseListState(
    val exercises: List<Exercise> = emptyList(),
    val groups: List<String> = emptyList(),
)

class ExerciseListViewModel(private val repository: ExerciseRepository) : BaseViewModel() {
    // État Compose (et non StateFlow) pour que le champ de recherche reste synchrone pendant la saisie.
    var query by mutableStateOf("")
    var selectedGroup by mutableStateOf<String?>(null)
        private set

    val state: StateFlow<ExerciseListState> = combine(
        repository.observeAll(),
        snapshotFlow { query.trim() },
        snapshotFlow { selectedGroup },
    ) { all, search, group ->
        ExerciseListState(
            exercises = all.filter {
                (group == null || it.muscleGroup == group) && (search.isEmpty() || it.name.contains(search, ignoreCase = true))
            },
            groups = all.mapNotNull { it.muscleGroup }.distinct().sorted(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExerciseListState())

    fun toggleGroup(group: String) {
        selectedGroup = if (selectedGroup == group) null else group
    }

    fun create(name: String, muscleGroup: String, description: String) = launchCatching {
        repository.create(name, muscleGroup, description)
    }

    companion object {
        val Factory = appViewModelFactory { ExerciseListViewModel(it.exerciseRepository) }
    }
}

@Composable
fun ExerciseListScreen(
    bottomBar: @Composable () -> Unit,
    onOpenExercise: (Long) -> Unit,
    viewModel: ExerciseListViewModel = viewModel(factory = ExerciseListViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    var showCreate by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Exercices") }) },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Exercice") },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = viewModel.query,
                onValueChange = { viewModel.query = it },
                placeholder = { Text("Rechercher un exercice") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.groups) { group ->
                    FilterChip(
                        selected = viewModel.selectedGroup == group,
                        onClick = { viewModel.toggleGroup(group) },
                        label = { Text(group) },
                    )
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                items(state.exercises, key = { it.id }) { exercise ->
                    val group = exercise.muscleGroup
                    ListItem(
                        headlineContent = { Text(exercise.name) },
                        supportingContent = if (group != null) {
                            { Text(group) }
                        } else {
                            null
                        },
                        modifier = Modifier.clickable { onOpenExercise(exercise.id) },
                    )
                }
            }
        }
    }

    if (showCreate) {
        ExerciseFormDialog(
            title = "Nouvel exercice",
            onDismiss = { showCreate = false },
            onConfirm = { name, group, description ->
                showCreate = false
                viewModel.create(name, group, description)
            },
        )
    }
}

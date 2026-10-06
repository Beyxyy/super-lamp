@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.programs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.SplitSummary
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.EmptyState
import fr.superlamp.mobile.ui.components.NameDescriptionDialog
import fr.superlamp.mobile.ui.plural
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class SplitListViewModel(private val repository: ProgramRepository) : BaseViewModel() {
    /** null tant que la base n'a pas répondu. */
    val splits: StateFlow<List<SplitSummary>?> = repository.observeSplits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun create(name: String, description: String, onCreated: (Long) -> Unit) = launchCatching {
        onCreated(repository.createSplit(name, description))
    }

    companion object {
        val Factory = appViewModelFactory { SplitListViewModel(it.programRepository) }
    }
}

@Composable
fun SplitListScreen(
    bottomBar: @Composable () -> Unit,
    onOpenSplit: (Long) -> Unit,
    viewModel: SplitListViewModel = viewModel(factory = SplitListViewModel.Factory),
) {
    val splits by viewModel.splits.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    var showCreate by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Programmes") }) },
        bottomBar = bottomBar,
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreate = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Programme") },
            )
        },
    ) { padding ->
        val list = splits
        when {
            list == null -> Unit
            list.isEmpty() -> EmptyState(
                icon = Icons.Default.CalendarMonth,
                title = "Aucun programme",
                message = "Un programme (split) regroupe tes séances types, par exemple « PPL » avec Push, Pull et Legs.",
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(list, key = { it.split.id }) { summary ->
                    ElevatedCard(onClick = { onOpenSplit(summary.split.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(summary.split.name, style = MaterialTheme.typography.titleMedium)
                            summary.split.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(
                                plural(summary.workoutCount, "séance"),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreate) {
        NameDescriptionDialog(
            title = "Nouveau programme",
            nameLabel = "Nom (ex. PPL)",
            onDismiss = { showCreate = false },
            onConfirm = { name, description ->
                showCreate = false
                viewModel.create(name, description, onOpenSplit)
            },
        )
    }
}

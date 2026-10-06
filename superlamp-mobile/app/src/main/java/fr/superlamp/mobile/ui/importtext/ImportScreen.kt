@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package fr.superlamp.mobile.ui.importtext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.core.ParsedExercise
import fr.superlamp.mobile.core.ParsedWorkout
import fr.superlamp.mobile.core.ParserService
import fr.superlamp.mobile.data.entity.WorkoutWithSplit
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.data.repository.ProgramRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.BackButton
import fr.superlamp.mobile.ui.formatSet
import fr.superlamp.mobile.ui.longDay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

private const val EXAMPLE = """27/06:
Élévation poulie : 5*30 5*30 15*20
Reverse fly : 8*2 7*2 6*2
Traction : 8 8 6"""

sealed interface ParsePreview {
    data object Empty : ParsePreview
    data class Error(val message: String) : ParsePreview
    data class Ready(
        val workout: ParsedWorkout,
        val valid: List<ParsedExercise>,
        val ignored: Int,
    ) : ParsePreview
}

class ImportViewModel(
    programRepository: ProgramRepository,
    private val liftRepository: LiftRepository,
    private val parser: ParserService = ParserService(),
) : BaseViewModel() {
    var text by mutableStateOf("")
    var selectedWorkout by mutableStateOf<WorkoutWithSplit?>(null)

    val workouts: StateFlow<List<WorkoutWithSplit>> = programRepository.observeAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Aperçu recalculé à chaque frappe (le parsing est instantané). */
    val preview: ParsePreview by derivedStateOf { computePreview(text) }

    private fun computePreview(input: String): ParsePreview {
        if (input.isBlank()) return ParsePreview.Empty
        return try {
            val parsed = parser.parse(input)
            val valid = parsed.exercises
                .map { exercise -> exercise.copy(sets = exercise.sets.filter { it.reps > 0 }) }
                .filter { it.name.isNotBlank() && it.sets.isNotEmpty() }
            ParsePreview.Ready(parsed, valid, ignored = parsed.exercises.size - valid.size)
        } catch (e: IllegalArgumentException) {
            ParsePreview.Error(e.message ?: "Texte invalide")
        }
    }

    fun save(onSaved: (Long) -> Unit) = launchCatching {
        val ready = preview as? ParsePreview.Ready ?: return@launchCatching
        onSaved(liftRepository.importParsed(ready.workout.date, ready.valid, selectedWorkout?.workout))
    }

    companion object {
        val Factory = appViewModelFactory { ImportViewModel(it.programRepository, it.liftRepository) }
    }
}

@Composable
fun ImportScreen(
    onBack: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: ImportViewModel = viewModel(factory = ImportViewModel.Factory),
) {
    val workouts by viewModel.workouts.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)
    val preview = viewModel.preview

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Importer une séance") },
                navigationIcon = { BackButton(onBack) },
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Colle tes notes de séance. Une ligne par exercice, les séries « reps*poids » séparées par des espaces. La date (jj/mm) en tête est optionnelle.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(12.dp)) {
                    Text(EXAMPLE, fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { viewModel.text = EXAMPLE }) { Text("Utiliser cet exemple") }
                }
            }
            OutlinedTextField(
                value = viewModel.text,
                onValueChange = { viewModel.text = it },
                label = { Text("Notes de séance") },
                minLines = 6,
                modifier = Modifier.fillMaxWidth(),
            )
            WorkoutSelector(
                workouts = workouts,
                selected = viewModel.selectedWorkout,
                onSelect = { viewModel.selectedWorkout = it },
            )
            PreviewCard(preview)
            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = preview is ParsePreview.Ready && preview.valid.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Enregistrer la séance")
            }
        }
    }
}

@Composable
private fun WorkoutSelector(
    workouts: List<WorkoutWithSplit>,
    selected: WorkoutWithSplit?,
    onSelect: (WorkoutWithSplit?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("Séance type (optionnel)", style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.splitName} · ${it.workout.name}" } ?: "Aucune (séance libre)",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("Aucune (séance libre)") },
                    onClick = {
                        onSelect(null)
                        expanded = false
                    },
                )
                workouts.forEach { workout ->
                    DropdownMenuItem(
                        text = { Text("${workout.splitName} · ${workout.workout.name}") },
                        onClick = {
                            onSelect(workout)
                            expanded = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewCard(preview: ParsePreview) {
    when (preview) {
        ParsePreview.Empty -> Unit
        is ParsePreview.Error -> Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(preview.message, modifier = Modifier.padding(16.dp))
        }
        is ParsePreview.Ready -> OutlinedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Aperçu", style = MaterialTheme.typography.titleSmall)
                Text(preview.workout.date.longDay(), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                if (preview.valid.isEmpty()) {
                    Text("Aucune série reconnue pour l'instant.", style = MaterialTheme.typography.bodySmall)
                }
                preview.valid.forEach { exercise ->
                    Column {
                        Text(exercise.name, style = MaterialTheme.typography.titleSmall)
                        Text(
                            exercise.sets.joinToString(" · ") { formatSet(it.reps, it.weight) },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                if (preview.ignored > 0) {
                    Text(
                        "${preview.ignored} ligne(s) ignorée(s) : nom ou séries manquants.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

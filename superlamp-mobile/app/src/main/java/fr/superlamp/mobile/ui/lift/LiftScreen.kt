@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package fr.superlamp.mobile.ui.lift

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.Lift
import fr.superlamp.mobile.data.entity.LiftSet
import fr.superlamp.mobile.ui.CollectMessages
import fr.superlamp.mobile.ui.components.BackButton
import fr.superlamp.mobile.ui.components.ConfirmDialog
import fr.superlamp.mobile.ui.components.ExercisePickerDialog
import fr.superlamp.mobile.ui.components.FormDialog
import fr.superlamp.mobile.ui.components.NameDescriptionDialog
import fr.superlamp.mobile.ui.components.NumberField
import fr.superlamp.mobile.ui.formatClock
import fr.superlamp.mobile.ui.formatDuration
import fr.superlamp.mobile.ui.formatSet
import fr.superlamp.mobile.ui.formatSetsCompact
import fr.superlamp.mobile.ui.formatVolume
import fr.superlamp.mobile.ui.formatWeightNumber
import fr.superlamp.mobile.ui.parseWeight
import fr.superlamp.mobile.ui.plannedLabel
import fr.superlamp.mobile.ui.shortDay
import fr.superlamp.mobile.ui.time
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun LiftScreen(
    onBack: () -> Unit,
    viewModel: LiftViewModel = viewModel(factory = LiftViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val rest by viewModel.rest.collectAsStateWithLifecycle()
    val catalogue by viewModel.catalogue.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    CollectMessages(viewModel, snackbar)

    var showPicker by rememberSaveable { mutableStateOf(false) }
    var showRename by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showFinish by rememberSaveable { mutableStateOf(false) }
    var editingSet by remember { mutableStateOf<LiftSet?>(null) }

    val lift = state.lift
    val active = lift != null && lift.endTime == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(lift?.name.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (lift != null) {
                            Text(
                                "${lift.startTime.shortDay()} · ${lift.startTime.time()}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                navigationIcon = { BackButton(onBack) },
                actions = {
                    if (lift != null) {
                        IconButton(onClick = { showRename = true }) { Icon(Icons.Default.Edit, contentDescription = "Modifier") }
                        IconButton(onClick = { showDelete = true }) { Icon(Icons.Default.Delete, contentDescription = "Supprimer") }
                    }
                },
            )
        },
        bottomBar = {
            if (active) {
                LiftBottomBar(
                    rest = rest,
                    onExtend = { viewModel.extendRest(15) },
                    onSkip = { viewModel.skipRest() },
                    onFinish = { showFinish = true },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        if (lift != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .imePadding(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header") {
                    LiftHeader(lift, setCount = state.setCount, volume = state.volume)
                }
                if (state.loaded && state.blocks.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            "Ajoute un premier exercice pour commencer à noter tes séries.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                items(state.blocks, key = { it.exerciseId }) { block ->
                    ExerciseBlockCard(
                        block = block,
                        onAddSet = { reps, weight -> viewModel.addSet(block, reps, weight) },
                        onEditSet = { editingSet = it },
                        onDeleteSet = { viewModel.deleteSet(it) },
                    )
                }
                item(key = "add") {
                    OutlinedButton(onClick = { showPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.size(8.dp))
                        Text("Ajouter un exercice")
                    }
                }
            }
        }
    }

    if (showPicker) {
        val shown = state.blocks.map { it.exerciseId }.toSet()
        ExercisePickerDialog(
            exercises = catalogue.filter { it.id !in shown },
            onDismiss = { showPicker = false },
            onPick = {
                showPicker = false
                viewModel.addExercise(it)
            },
            onCreate = {
                showPicker = false
                viewModel.createExercise(it)
            },
        )
    }
    if (showRename && lift != null) {
        NameDescriptionDialog(
            title = "Modifier la séance",
            initialName = lift.name,
            initialDescription = lift.description.orEmpty(),
            descriptionLabel = "Notes (optionnel)",
            onDismiss = { showRename = false },
            onConfirm = { name, description ->
                showRename = false
                viewModel.rename(name, description)
            },
        )
    }
    if (showDelete) {
        ConfirmDialog(
            title = "Supprimer la séance ?",
            message = "La séance et toutes ses séries seront supprimées.",
            onDismiss = { showDelete = false },
            onConfirm = { viewModel.delete(onBack) },
        )
    }
    if (showFinish) {
        ConfirmDialog(
            title = "Terminer la séance ?",
            message = "Tu pourras toujours modifier les séries ensuite depuis l'historique.",
            confirmLabel = "Terminer",
            onDismiss = { showFinish = false },
            onConfirm = { viewModel.finish() },
        )
    }
    editingSet?.let { set ->
        EditSetDialog(
            set = set,
            onDismiss = { editingSet = null },
            onConfirm = { reps, weight ->
                editingSet = null
                viewModel.updateSet(set, reps, weight)
            },
        )
    }
}

@Composable
private fun LiftHeader(lift: Lift, setCount: Int, volume: Double) {
    val end = lift.endTime
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    if (end == null) {
        LaunchedEffect(lift.id) {
            while (true) {
                now = LocalDateTime.now()
                delay(1_000)
            }
        }
    }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (end == null) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeaderStat(
                label = if (end == null) "En cours" else "Durée",
                value = if (end == null) formatClock(Duration.between(lift.startTime, now)) else formatDuration(Duration.between(lift.startTime, end)),
            )
            HeaderStat("Séries", setCount.toString())
            HeaderStat("Volume", formatVolume(volume))
        }
        lift.description?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun HeaderStat(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ExerciseBlockCard(
    block: ExerciseBlock,
    onAddSet: (Int?, Double?) -> Unit,
    onEditSet: (LiftSet) -> Unit,
    onDeleteSet: (LiftSet) -> Unit,
) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(block.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                block.plannedSets?.let { planned ->
                    Text(
                        "${block.sets.size}/$planned",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (block.sets.size >= planned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            block.plannedSets?.let { planned ->
                Text(
                    "Objectif : ${plannedLabel(planned, block.plannedReps, block.restTimeSeconds)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (block.previous.isNotEmpty()) {
                Text(
                    "Dernière fois : ${formatSetsCompact(block.previous)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            block.sets.forEachIndexed { index, set ->
                SetRow(
                    position = index + 1,
                    set = set,
                    onClick = { onEditSet(set) },
                    onDelete = { onDeleteSet(set) },
                )
            }
            SetInput(block, onAddSet)
        }
    }
}

@Composable
private fun SetRow(position: Int, set: LiftSet, onClick: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("$position", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(Modifier.size(12.dp))
        Text(formatSet(set.reps, set.weightKg), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Supprimer la série", modifier = Modifier.size(18.dp))
        }
    }
}

/** Saisie d'une série, pré-remplie avec la série précédente (ou la dernière séance). */
@Composable
private fun SetInput(block: ExerciseBlock, onAdd: (Int?, Double?) -> Unit) {
    val template = block.sets.lastOrNull() ?: block.previous.firstOrNull()
    val defaultReps = template?.reps ?: block.plannedReps
    var reps by remember(block.exerciseId, block.sets.size, template?.id) {
        mutableStateOf(defaultReps?.toString().orEmpty())
    }
    var weight by remember(block.exerciseId, block.sets.size, template?.id) {
        mutableStateOf(template?.weightKg?.let(::formatWeightNumber).orEmpty())
    }
    val repsValue = reps.toIntOrNull()

    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        NumberField(reps, { reps = it }, "Reps", Modifier.weight(1f))
        NumberField(weight, { weight = it }, "Poids (kg)", Modifier.weight(1f), decimal = true)
        FilledIconButton(
            onClick = { onAdd(repsValue, parseWeight(weight)) },
            enabled = repsValue != null && repsValue > 0,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Ajouter la série")
        }
    }
}

@Composable
private fun LiftBottomBar(
    rest: RestTimer?,
    onExtend: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit,
) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (rest != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        "Repos ${formatClock(Duration.ofSeconds(rest.remaining.toLong()))}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onExtend) { Text("+15 s") }
                    TextButton(onClick = onSkip) { Text("Passer") }
                }
                LinearProgressIndicator(
                    progress = { rest.remaining.toFloat() / rest.total.coerceAtLeast(1) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Terminer la séance")
            }
        }
    }
}

@Composable
private fun EditSetDialog(
    set: LiftSet,
    onDismiss: () -> Unit,
    onConfirm: (Int?, Double?) -> Unit,
) {
    var reps by rememberSaveable { mutableStateOf(set.reps.toString()) }
    var weight by rememberSaveable { mutableStateOf(set.weightKg?.let(::formatWeightNumber).orEmpty()) }
    val repsValue = reps.toIntOrNull()
    FormDialog(
        title = "Modifier la série",
        onDismiss = onDismiss,
        onConfirm = { onConfirm(repsValue, parseWeight(weight)) },
        confirmEnabled = repsValue != null && repsValue > 0,
    ) {
        NumberField(reps, { reps = it }, "Répétitions", Modifier.fillMaxWidth())
        NumberField(weight, { weight = it }, "Poids en kg (vide = poids du corps)", Modifier.fillMaxWidth(), decimal = true)
    }
}

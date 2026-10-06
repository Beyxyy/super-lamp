@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import fr.superlamp.mobile.data.MUSCLE_GROUPS
import fr.superlamp.mobile.data.entity.Exercise
import fr.superlamp.mobile.data.entity.LiftSummary
import fr.superlamp.mobile.ui.formatDuration
import fr.superlamp.mobile.ui.formatVolume
import fr.superlamp.mobile.ui.plural
import fr.superlamp.mobile.ui.shortDay
import fr.superlamp.mobile.ui.time
import java.time.Duration

@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
    }
}

@Composable
fun FormDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String = "Enregistrer",
    confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = content) },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = confirmEnabled) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmLabel: String = "Supprimer",
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                onDismiss()
                onConfirm()
            }) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
    )
}

/** Dialogue « nom + description », utilisé pour les programmes, séances types et séances. */
@Composable
fun NameDescriptionDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, description: String) -> Unit,
    nameLabel: String = "Nom",
    descriptionLabel: String = "Description (optionnel)",
    initialName: String = "",
    initialDescription: String = "",
) {
    var name by rememberSaveable { mutableStateOf(initialName) }
    var description by rememberSaveable { mutableStateOf(initialDescription) }
    FormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(name, description) },
        confirmEnabled = name.isNotBlank(),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(nameLabel) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(descriptionLabel) },
            minLines = 2,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun ExerciseFormDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, muscleGroup: String, description: String) -> Unit,
    initial: Exercise? = null,
) {
    var name by rememberSaveable { mutableStateOf(initial?.name.orEmpty()) }
    var muscleGroup by rememberSaveable { mutableStateOf(initial?.muscleGroup.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(initial?.description.orEmpty()) }
    FormDialog(
        title = title,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(name, muscleGroup, description) },
        confirmEnabled = name.isNotBlank(),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = muscleGroup,
            onValueChange = { muscleGroup = it },
            label = { Text("Groupe musculaire") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = Modifier.fillMaxWidth(),
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(MUSCLE_GROUPS) { group ->
                SuggestionChip(onClick = { muscleGroup = group }, label = { Text(group) })
            }
        }
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description (optionnel)") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Champ numérique : n'accepte que des chiffres (et la virgule si [decimal]). */
@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { new ->
            if (new.all { it.isDigit() || (decimal && (it == ',' || it == '.')) }) onValueChange(new)
        },
        label = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number),
        modifier = modifier,
    )
}

/** Choix d'un exercice du catalogue, avec création à la volée. */
@Composable
fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onPick: (Exercise) -> Unit,
    onCreate: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val search = query.trim()
    val filtered = remember(exercises, search) {
        exercises.filter {
            it.name.contains(search, ignoreCase = true) || it.muscleGroup?.contains(search, ignoreCase = true) == true
        }
    }
    val exactMatch = exercises.any { it.name.equals(search, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choisir un exercice") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Rechercher ou créer…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.size(8.dp))
                LazyColumn(Modifier.heightIn(max = 360.dp)) {
                    if (search.isNotEmpty() && !exactMatch) {
                        item {
                            ListItem(
                                headlineContent = { Text("Créer « $search »") },
                                leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                                modifier = Modifier.clickable { onCreate(search) },
                            )
                        }
                    }
                    items(filtered, key = { it.id }) { exercise ->
                        val group = exercise.muscleGroup
                        ListItem(
                            headlineContent = { Text(exercise.name) },
                            supportingContent = if (group != null) {
                                { Text(group) }
                            } else {
                                null
                            },
                            modifier = Modifier.clickable { onPick(exercise) },
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        },
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(
            message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        action?.invoke()
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun LiftSummaryCard(summary: LiftSummary, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val lift = summary.lift
    val end = lift.endTime
    ElevatedCard(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(lift.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    buildString {
                        append(lift.startTime.shortDay())
                        append(" · ")
                        append(lift.startTime.time())
                        append(" · ")
                        append(if (end == null) "en cours" else formatDuration(Duration.between(lift.startTime, end)))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "${plural(summary.exerciseCount, "exercice")} · ${plural(summary.setCount, "série")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (summary.volume > 0) {
                Text(
                    formatVolume(summary.volume),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/** Courbe simple (points reliés) ; [values] dans l'ordre chronologique. */
@Composable
fun ProgressChart(values: List<Double>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    Canvas(modifier) {
        if (values.size < 2) return@Canvas
        val inset = 8.dp.toPx()
        val width = size.width - 2 * inset
        val height = size.height - 2 * inset
        val min = values.min()
        val max = values.max()
        val range = max - min
        val stepX = width / (values.size - 1)
        fun point(index: Int, value: Double): Offset {
            val ratio = if (range > 0) ((value - min) / range).toFloat() else 0.5f
            return Offset(inset + index * stepX, inset + height * (1f - ratio))
        }

        drawLine(gridColor, Offset(inset, inset), Offset(inset + width, inset), strokeWidth = 1.dp.toPx())
        drawLine(gridColor, Offset(inset, inset + height), Offset(inset + width, inset + height), strokeWidth = 1.dp.toPx())

        val path = Path()
        values.forEachIndexed { index, value ->
            val p = point(index, value)
            if (index == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        drawPath(
            path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
        values.forEachIndexed { index, value ->
            drawCircle(lineColor, radius = 4.dp.toPx(), center = point(index, value))
        }
    }
}

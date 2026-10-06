@file:OptIn(ExperimentalMaterial3Api::class)

package fr.superlamp.mobile.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.superlamp.mobile.data.entity.LiftSummary
import fr.superlamp.mobile.data.repository.LiftRepository
import fr.superlamp.mobile.ui.BaseViewModel
import fr.superlamp.mobile.ui.appViewModelFactory
import fr.superlamp.mobile.ui.components.EmptyState
import fr.superlamp.mobile.ui.components.LiftSummaryCard
import fr.superlamp.mobile.ui.label
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth

class HistoryViewModel(liftRepository: LiftRepository) : BaseViewModel() {
    /** Séances groupées par mois, de la plus récente à la plus ancienne. null = chargement. */
    val months: StateFlow<List<Pair<YearMonth, List<LiftSummary>>>?> = liftRepository.observeSummaries()
        .map { lifts -> lifts.groupBy { YearMonth.from(it.lift.startTime) }.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    companion object {
        val Factory = appViewModelFactory { HistoryViewModel(it.liftRepository) }
    }
}

@Composable
fun HistoryScreen(
    bottomBar: @Composable () -> Unit,
    onOpenLift: (Long) -> Unit,
    onImport: () -> Unit,
    viewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory),
) {
    val months by viewModel.months.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historique") },
                actions = {
                    IconButton(onClick = onImport) {
                        Icon(Icons.Default.ContentPaste, contentDescription = "Importer une séance")
                    }
                },
            )
        },
        bottomBar = bottomBar,
    ) { padding ->
        val list = months
        when {
            list == null -> Unit
            list.isEmpty() -> EmptyState(
                icon = Icons.Default.History,
                title = "Aucune séance",
                message = "Démarre une séance depuis l'accueil, ou importe tes anciennes notes.",
                modifier = Modifier.padding(padding),
                action = { TextButton(onClick = onImport) { Text("Importer depuis un texte") } },
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                list.forEach { (month, lifts) ->
                    item(key = "month-$month") {
                        Text(
                            month.label(),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    items(lifts, key = { it.lift.id }) { summary ->
                        LiftSummaryCard(summary, onClick = { onOpenLift(summary.lift.id) })
                    }
                }
            }
        }
    }
}

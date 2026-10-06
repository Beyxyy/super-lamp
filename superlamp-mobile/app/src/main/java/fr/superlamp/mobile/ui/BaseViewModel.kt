package fr.superlamp.mobile.ui

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import fr.superlamp.mobile.AppContainer
import fr.superlamp.mobile.SuperLampApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * Les règles métier lèvent des IllegalArgumentException (comme les `require` du backend) :
 * on les transforme en messages affichés dans une snackbar.
 */
abstract class BaseViewModel : ViewModel() {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    protected fun notify(message: String) {
        _messages.tryEmit(message)
    }

    protected fun launchCatching(block: suspend CoroutineScope.() -> Unit): Job = viewModelScope.launch {
        try {
            block()
        } catch (e: IllegalArgumentException) {
            notify(e.message ?: "Valeur invalide")
        } catch (e: SQLiteConstraintException) {
            notify("Opération impossible : cette donnée existe déjà")
        }
    }
}

/** Fabrique de ViewModel ayant accès aux repositories de l'application. */
inline fun <reified VM : ViewModel> appViewModelFactory(
    crossinline create: CreationExtras.(AppContainer) -> VM,
): ViewModelProvider.Factory = viewModelFactory {
    initializer {
        val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SuperLampApp
        this.create(app.container)
    }
}

@Composable
fun CollectMessages(viewModel: BaseViewModel, snackbarHostState: SnackbarHostState) {
    LaunchedEffect(viewModel) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }
}

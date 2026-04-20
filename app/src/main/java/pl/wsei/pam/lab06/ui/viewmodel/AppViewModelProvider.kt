package pl.wsei.pam.lab06.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import pl.wsei.pam.lab06.TodoApplication

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = todoApplication().container
            ListViewModel(repository = container.todoTaskRepository)
        }

        initializer {
            val container = todoApplication().container
            FormViewModel(
                repository = container.todoTaskRepository,
                currentDateProvider = container.currentDateProvider
            )
        }
    }
}

fun CreationExtras.todoApplication(): TodoApplication {
    val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
    return application as TodoApplication
}

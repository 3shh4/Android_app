package pl.wsei.pam.lab06.model

import java.time.LocalDate

data class TodoTask(
    val title: String = "",
    val deadline: LocalDate = LocalDate.now(),
    val isDone: Boolean = false,
    val priority: Priority = Priority.Low,
    val id: Int = 0
)

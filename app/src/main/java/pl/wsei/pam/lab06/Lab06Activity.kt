    package pl.wsei.pam.lab06

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.CircleShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowBack
    import androidx.compose.material.icons.filled.Add
    import androidx.compose.material.icons.filled.Home
    import androidx.compose.material.icons.filled.Settings
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.runtime.saveable.rememberSaveable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController
    import androidx.navigation.compose.*
    import java.time.Instant
    import java.time.LocalDate
    import java.time.ZoneId
    import java.time.format.DateTimeFormatter

    class Lab06Activity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                MaterialTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MainScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "list") {
            composable("list") { ListScreen(navController) }
            composable("form") { FormScreen(navController) }
        }
    }

    fun todoTasks(): List<TodoTask> {
        return listOf(
            TodoTask("Programming", LocalDate.of(2024, 4, 18), false, Priority.Low),
            TodoTask("Teaching", LocalDate.of(2024, 5, 12), false, Priority.High),
            TodoTask("Learning", LocalDate.of(2024, 6, 28), true, Priority.Low),
            TodoTask("Cooking", LocalDate.of(2024, 8, 18), false, Priority.Medium),
        )
    }

    enum class Priority {
        High, Medium, Low
    }

    data class TodoTask(
        val title: String,
        val deadline: LocalDate,
        val isDone: Boolean,
        val priority: Priority
    )

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppTopBar(
        navController: NavController,
        title: String,
        showBackIcon: Boolean,
        route: String
    ) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                if (showBackIcon) {
                    IconButton(onClick = { navController.navigate(route) }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                if (route != "form") {
                    OutlinedButton(onClick = { navController.navigate("list") }) {
                        Text("Zapisz")
                    }
                } else {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Settings, "")
                    }
                    IconButton(onClick = { navController.navigate("list") }) {
                        Icon(Icons.Default.Home, "")
                    }
                }
            }
        )
    }

    @Composable
    fun ListScreen(navController: NavController) {
        Scaffold(
            topBar = {
                AppTopBar(navController, "List", false, "form")
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("form") },
                    shape = CircleShape
                ) {
                    Icon(Icons.Filled.Add, "Add")
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(todoTasks()) { item ->
                    ListItem(item)
                }
            }
        }
    }

    @Composable
    fun ListItem(item: TodoTask) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .heightIn(min = 120.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(item.title, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Deadline:")
                    Text(item.deadline.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Priority:")
                    Text(item.priority.name)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Status:")
                    Text(if (item.isDone) "Done" else "In progress")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FormScreen(navController: NavController) {

        var title by rememberSaveable { mutableStateOf("") }
        var isDone by rememberSaveable { mutableStateOf(false) }
        var priority by rememberSaveable { mutableStateOf(Priority.Low) }
        var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                AppTopBar(navController, "Form", true, "list")
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(isDone, { isDone = it })
                    Text("Done")
                }

                Row {
                    RadioButton(priority == Priority.High, { priority = Priority.High })
                    Text("High")

                    Spacer(Modifier.width(10.dp))

                    RadioButton(priority == Priority.Medium, { priority = Priority.Medium })
                    Text("Medium")

                    Spacer(Modifier.width(10.dp))

                    RadioButton(priority == Priority.Low, { priority = Priority.Low })
                    Text("Low")
                }

                OutlinedButton(onClick = { showDialog = true }) {
                    Text(selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                }

                Button(
                    onClick = { navController.navigate("list") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save")
                }
            }

            if (showDialog) {
                val state = rememberDatePickerState()

                DatePickerDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            state.selectedDateMillis?.let {
                                selectedDate = Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            }
                            showDialog = false
                        }) { Text("OK") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { showDialog = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state)
                }
            }
        }
    }
package presentation.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.RequestState
import domain.TaskAction
import domain.ToDoTask
import presentation.components.ErrorScreen
import presentation.components.LoadingScreen
import presentation.components.TaskView
import presentation.screens.task.TaskScreen


class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()
        val activeTasks by viewModel.activeTasks
        val completedTasks by viewModel.completedTasks

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Notes", color = Color.White, fontWeight = FontWeight.Medium)
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navigator.push(TaskScreen())
                    },
                    shape = RoundedCornerShape(size = 12.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Edit, contentDescription = "Edit Icon")
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(top = 24.dp)
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
            ) {

                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = activeTasks,
                    onSelectedTask = { task ->
                        navigator.push(TaskScreen(task))
                    },
                    onFavoriteTask = { task, favorite ->
                        viewModel.setAction(
                            TaskAction.SetFavorite(
                                task = task,
                                isFavorite = favorite
                            )
                        )
                    },
                    onCompleteTask = { task, complete ->
                        viewModel.setAction(
                            TaskAction.SetCompleted(
                                task = task,
                                completed = complete
                            )
                        )
                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                DisplayTasks(
                    modifier = Modifier.weight(1f),
                    tasks = completedTasks,
                    showActiveTasks = false,
                    onCompleteTask = { task, complete ->
                        viewModel.setAction(
                            TaskAction.SetCompleted(
                                task = task,
                                completed = complete
                            )
                        )
                    },
                    onDeleteTask = { task ->
                        viewModel.setAction(
                            TaskAction.Delete(
                                task = task,
                            )
                        )
                    }
                )

            }


        }

    }

}

@Composable
fun DisplayTasks(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<ToDoTask>>,
    showActiveTasks: Boolean = true,
    onSelectedTask: ((ToDoTask) -> Unit)? = null,
    onFavoriteTask: ((ToDoTask, Boolean) -> Unit)? = null,
    onCompleteTask: (ToDoTask, Boolean) -> Unit,
    onDeleteTask: ((ToDoTask) -> Unit)? = null,
) {

    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: ToDoTask? by remember { mutableStateOf(null) }

    if (showDialog) {
        AlertDialog(
            title = {
                Text(text = "Delete Task", fontSize = MaterialTheme.typography.titleLarge.fontSize)
            },
            text = {
                Text(
                    text = "Are you sure to delete '${taskToDelete?.title}'",
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTask?.invoke(taskToDelete!!)
                        showDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            onDismissRequest = {
                showDialog = false
                taskToDelete = null
            }
        )
    }

        Column(
            modifier = modifier.fillMaxWidth()
        ) {

            Text(
                modifier = Modifier.padding(horizontal = 12.dp),
                text = if (showActiveTasks) "Active Tasks" else "Completed Tasks",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.Medium
            )

            Card(
                modifier = modifier.padding(horizontal = 14.dp, vertical = 10.dp).fillMaxSize(),
                shape = RoundedCornerShape(size = 30.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
            tasks.DisplayResult(
                onLoading = { LoadingScreen() },
                onError = { ErrorScreen(message = it) },
                onSuccess = {
                    if (it.isNotEmpty()) {

                        LazyColumn(modifier = modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
                            items(
                                items = it,
                                key = { task -> task._id.toHexString() }
                            ) { task ->

                                TaskView(
                                    showActiveTask = showActiveTasks,
                                    task = task,
                                    onSelect = { selectedTask ->
                                        onSelectedTask?.invoke(selectedTask)
                                    },
                                    onComplete = { completedTask, complete ->
                                        onCompleteTask.invoke(completedTask, complete)
                                    },
                                    onFavorite = { favoriteTask, favorite ->
                                        onFavoriteTask?.invoke(favoriteTask, favorite)
                                    },
                                    onDelete = { deleteTask ->
                                        taskToDelete = deleteTask
                                        showDialog = true
                                    }
                                )

                            }
                        }

                    } else {
                        ErrorScreen()
                    }
                }
            )

        }
    }


}
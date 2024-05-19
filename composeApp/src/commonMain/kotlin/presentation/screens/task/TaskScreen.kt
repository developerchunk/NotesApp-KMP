package presentation.screens.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.TaskAction
import domain.ToDoTask

const val DEFAULT_TITLE = "Enter the Title"
const val DEFAULT_DESCRIPTION = "Add some description"

data class TaskScreen(val task: ToDoTask? = null) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val taskViewModel = getScreenModel<TaskViewModel>()
        var currentTitle by remember {
            mutableStateOf(task?.title ?: DEFAULT_TITLE)
        }
        var currentDescription by remember {
            mutableStateOf(task?.description ?: DEFAULT_DESCRIPTION)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        BasicTextField(
                            textStyle = TextStyle(
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            singleLine = true,
                            value = currentTitle,
                            onValueChange = {
                                currentTitle = it
                            }
                        )

                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navigator.pop()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back Arrow",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotEmpty() && currentDescription.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            if (task != null) {
                                // update
                                taskViewModel.setAction(
                                    action = TaskAction.Update(
                                        task = ToDoTask().apply {
                                            _id = task._id
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            } else {
                                // add
                                taskViewModel.setAction(
                                    action = TaskAction.Add(
                                        task = ToDoTask().apply {
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            }
                            navigator.pop()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Done - Checkmark Icon"
                        )
                    }
                }
            }
        ) { paddingValues ->

            Card(
                modifier = Modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        bottom = paddingValues.calculateBottomPadding()
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
                    .fillMaxSize(),
                shape = RoundedCornerShape(size = 30.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {

                BasicTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    ),
                    value = currentDescription,
                    onValueChange = {
                        currentDescription = it
                    }
                )

            }
        }

    }

}
package com.aman.mynoteapp.feature_note.presentation.notes

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aman.mynoteapp.feature_note.presentation.notes.components.NoteItem
import com.aman.mynoteapp.feature_note.presentation.notes.components.OrderSection
import com.aman.mynoteapp.feature_note.presentation.util.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalAnimationApi
@Composable
fun NotesScreen(
    navController: NavController,
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
//    val scaffoldState = rememberScaffoldState()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val currentContext = LocalContext.current

    val constraints = ConstraintSet {
        val columnParent = createRefFor("columnParent")
        val floatingActionButtonAddNote = createRefFor("floatingActionButtonAddNote")

        constrain(floatingActionButtonAddNote) {
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }

    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            MediumTopAppBar(title = { Text(text = "My Note") }, actions = {
                IconButton(
                    onClick = {
                        viewModel.onEvent(NotesEvent.ToggleOrderSection)
                    }, colors = IconButtonDefaults.iconButtonColors()
                ) {
                    Icon(
                        imageVector = Icons.Default.Sort,
                        contentDescription = "Sort"
                    )
                }
            },
            scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
            colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .layoutId("floatingActionButtonAddNote")
                    .padding(all = 20.dp),
                shape = FloatingActionButtonDefaults.shape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .layoutId("columnParent")
            ) {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Your note",
//                        style = MaterialTheme.typography.headlineSmall
//                    )
//                    IconButton(
//                        onClick = {
//                            viewModel.onEvent(NotesEvent.ToggleOrderSection)
//                        },
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.Sort,
//                            contentDescription = "Sort"
//                        )
//                    }
//                }
                AnimatedVisibility(
                    visible = state.isOrderSectionVisible,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    OrderSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        noteOrder = state.noteOrder,
                        onOrderChange = {
                            viewModel.onEvent(NotesEvent.Order(it))
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.notes) { note ->
                        NoteItem(
                            note = note,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(
                                        Screen.AddEditNoteScreen.route +
                                                "?noteId=${note.id}&noteColor=${note.color}"
                                    )
                                },
                            onDeleteClick = {
                                viewModel.onEvent(NotesEvent.DeleteNote(note))

                                scope.launch {
                                    val result = snackBarHostState.showSnackbar(
                                        message = "Note deleted",
                                        actionLabel = "Undo",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        viewModel.onEvent(NotesEvent.RestoreNote)
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}


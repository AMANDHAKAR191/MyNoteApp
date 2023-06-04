package com.aman.mynoteapp.feature_note.presentation.add_edit_note

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aman.mynoteapp.feature_note.domain.model.Note
import com.aman.mynoteapp.feature_note.presentation.add_edit_note.components.TransparentHintTextField
import com.aman.mynoteapp.feature_note.presentation.notes.NotesEvent
import com.aman.mynoteapp.feature_note.presentation.notes.components.NoteItem
import com.aman.mynoteapp.feature_note.presentation.notes.components.OrderSection
import com.aman.mynoteapp.feature_note.presentation.util.Screen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value

    val snackBarHostState = remember { SnackbarHostState() }

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> {
                    snackBarHostState.showSnackbar(
                        message = event.message
                    )
                }

                is AddEditNoteViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            MediumTopAppBar(title = { Text(text = "My Note") },
                scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(),
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditNoteEvent.SaveNote)
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.layoutId("floatingActionButtonAddNote").padding(all = 20.dp)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(noteBackgroundAnimatable.value)
                    .padding(innerPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Note.noteColors.forEach { color ->
                        val colorInt = color.toArgb()
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .shadow(15.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = 3.dp,
                                    color = if (viewModel.noteColor.value == colorInt) {
                                        Color.Black
                                    } else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    scope.launch {
                                        noteBackgroundAnimatable.animateTo(
                                            targetValue = Color(colorInt),
                                            animationSpec = tween(
                                                durationMillis = 500
                                            )
                                        )
                                    }
                                    viewModel.onEvent(AddEditNoteEvent.ChangeColor(colorInt))
                                }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TransparentHintTextField(
                    text = titleState.text,
                    hint = titleState.hint,
                    onValueChange = {
                        viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it))
                    },
                    onFocusChange = {
                        viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it))
                    },
                    isHintVisible = titleState.isHintVisible,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                TransparentHintTextField(
                    text = contentState.text,
                    hint = contentState.hint,
                    onValueChange = {
                        viewModel.onEvent(AddEditNoteEvent.EnteredContent(it))
                    },
                    onFocusChange = {
                        viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it))
                    },
                    isHintVisible = contentState.isHintVisible,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    )
}
package app.khonkheree.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.khonkheree.data.api.BookOut
import app.khonkheree.ui.components.BookCard
import app.khonkheree.ui.components.statusLabel
import app.khonkheree.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onBookClick: (String) -> Unit,
    onAddBook: () -> Unit,
    vm: BookViewModel = hiltViewModel(),
) {
    val state by vm.library.collectAsState()

    LaunchedEffect(Unit) { vm.loadMyBooks() }

    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Миний номын сан", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            ) 
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddBook,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ном нэмэх")
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> Text(
                    state.error!!, color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
                state.books.isEmpty() -> Text(
                    "Номын сан хоосон байна.\n+ товч дарж ном нэмнэ үү.",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.books, key = { it.id }) { book ->
                        BookCard(book = book, onClick = { onBookClick(book.id) })
                    }
                }
            }
        }
    }
}


// BookCard and statusLabel removed as they are now in BookComponents.kt

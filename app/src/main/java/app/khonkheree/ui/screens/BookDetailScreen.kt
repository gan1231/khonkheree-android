package app.khonkheree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.khonkheree.data.api.ReviewOut
import app.khonkheree.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: String,
    onBack: () -> Unit,
    vm: BookViewModel = hiltViewModel(),
) {
    val state by vm.detail.collectAsState()
    var reviewText by remember { mutableStateOf("") }
    var showReviewSheet by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) { vm.loadBookDetail(bookId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.book?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Буцах")
                    }
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { showReviewSheet = true }) {
                Text("Сэтгэгдэл нэмэх")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val book = state.book ?: return@Scaffold

        LazyColumn(
            Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                // Book header
                Card {
                    Column(Modifier.padding(16.dp)) {
                        Text(book.title, style = MaterialTheme.typography.headlineSmall)
                        Text(book.author, style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        book.isbn?.let { Text("ISBN: $it", style = MaterialTheme.typography.bodySmall) }
                        Spacer(Modifier.height(4.dp))
                        book.synopsis?.let {
                            Text(it, style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AssistChip(onClick = {}, label = { Text(statusLabel(book.status)) })
                            book.sale_price?.let {
                                AssistChip(onClick = {}, label = { Text("₮${it.toInt()}") })
                            }
                        }
                    }
                }
            }

            item {
                Text("Сэтгэгдлүүд (${state.reviews.size})", style = MaterialTheme.typography.titleMedium)
            }

            if (state.reviews.isEmpty()) {
                item { Text("Сэтгэгдэл байхгүй байна.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            } else {
                items(state.reviews, key = { it.id }) { review ->
                    ReviewCard(review = review, onLike = { vm.toggleLike(review.id) })
                }
            }
        }
    }

    if (showReviewSheet) {
        ModalBottomSheet(onDismissRequest = { showReviewSheet = false }) {
            Column(Modifier.padding(16.dp).navigationBarsPadding()) {
                Text("Сэтгэгдэл бичих", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Сэтгэгдэл…") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        vm.addReview(bookId, reviewText)
                        reviewText = ""
                        showReviewSheet = false
                    },
                    enabled = reviewText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Нийтлэх") }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewOut, onLike: () -> Unit) {
    var liked by remember { mutableStateOf(false) }
    var count by remember { mutableIntStateOf(review.likes_count) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.author_name, style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                IconButton(onClick = {
                    liked = !liked
                    count += if (liked) 1 else -1
                    onLike()
                }) {
                    Icon(
                        if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text("$count", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(review.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

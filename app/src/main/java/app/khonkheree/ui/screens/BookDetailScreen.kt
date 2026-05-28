package app.khonkheree.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import app.khonkheree.data.api.ReviewOut
import app.khonkheree.ui.components.statusLabel
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
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Буцах")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showReviewSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier
                            .width(120.dp)
                            .aspectRatio(0.7f),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        tonalElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                book.title.take(1).uppercase(),
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        book.author,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SuggestionChip(
                            onClick = {},
                            label = { Text(statusLabel(book.status)) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        )
                        book.sale_price?.let {
                            SuggestionChip(
                                onClick = {},
                                label = { Text("₮${it.toInt()}") },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text("Тайлбар", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text(
                    book.synopsis ?: "Тайлбар байхгүй байна.",
                    style = MaterialTheme.typography.bodyLarge
                )
                book.isbn?.let { 
                    Spacer(Modifier.height(8.dp))
                    Text("ISBN: $it", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline) 
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text("Сэтгэгдлүүд (${state.reviews.size})", style = MaterialTheme.typography.titleLarge)
            }

            if (state.reviews.isEmpty()) {
                item { 
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Text(
                            "Сэтгэгдэл байхгүй байна. Анхных нь болоорой!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.reviews, key = { it.id }) { review ->
                    ReviewCard(review = review, onLike = { vm.toggleLike(review.id) })
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            review.author_name.take(1).uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    review.author_name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    liked = !liked
                    count += if (liked) 1 else -1
                    onLike()
                }) {
                    Icon(
                        if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                    )
                }
                Text(
                    "$count",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (liked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                review.content,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )
        }
    }
}

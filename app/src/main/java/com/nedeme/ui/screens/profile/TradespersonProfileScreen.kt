package com.nedeme.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nedeme.data.model.Review
import com.nedeme.ui.theme.FeaturedGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradespersonProfileScreen(
    uiState: ProfileUiState,
    onRequestService: (tradespersonId: String, category: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.tradesperson == null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(uiState.error ?: "Profil non trouvé") }
            }
            else -> {
                val tp = uiState.tradesperson
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // Header
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (tp.photoUrl != null) {
                                AsyncImage(
                                    model = tp.photoUrl,
                                    contentDescription = tp.displayName,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(MaterialTheme.shapes.large),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Surface(
                                    modifier = Modifier.size(100.dp),
                                    shape = MaterialTheme.shapes.large,
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.padding(24.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tp.displayName, style = MaterialTheme.typography.headlineMedium)
                                if (tp.isFeatured) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Default.WorkspacePremium,
                                        contentDescription = "Premium",
                                        tint = FeaturedGold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Text(tp.city, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = FeaturedGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("%.1f".format(tp.averageRating), style = MaterialTheme.typography.titleMedium)
                                Text(
                                    " (${tp.totalReviews} avis) • ${tp.completedJobs} travaux",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (tp.hourlyRate != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "${tp.hourlyRate.toInt()} FCFA/h",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    // Description
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("À propos", style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(tp.description, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    // Categories
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            tp.categories.forEach { cat ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(cat.replaceFirstChar { it.uppercase() }) }
                                )
                            }
                        }
                    }

                    // Request button
                    item {
                        Button(
                            onClick = {
                                onRequestService(
                                    tp.userId,
                                    tp.categories.firstOrNull() ?: ""
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Demander un service")
                        }
                    }

                    // Reviews section
                    if (uiState.reviews.isNotEmpty()) {
                        item {
                            Text(
                                "Avis (${uiState.reviews.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        items(uiState.reviews) { review ->
                            ReviewCard(review)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(review.clientName, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.weight(1f))
                repeat(review.rating) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = FeaturedGold,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (review.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(review.comment, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

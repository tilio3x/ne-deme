package com.nedeme.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nedeme.data.model.BookingRequest
import com.nedeme.data.model.BookingStatus
import com.nedeme.ui.theme.FeaturedGold
import com.nedeme.ui.theme.Success
import com.nedeme.ui.theme.Error as ErrorColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mes demandes") })
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            uiState.bookings.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Aucune demande",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(uiState.bookings) { booking ->
                        ClientBookingCard(booking)
                    }
                }
            }
        }
    }
}

@Composable
private fun ClientBookingCard(booking: BookingRequest) {
    val statusColor = when (booking.status) {
        BookingStatus.PENDING -> FeaturedGold
        BookingStatus.ACCEPTED -> Success
        BookingStatus.REJECTED, BookingStatus.CANCELLED -> ErrorColor
        BookingStatus.COMPLETED -> MaterialTheme.colorScheme.primary
    }
    val statusText = when (booking.status) {
        BookingStatus.PENDING -> "En attente"
        BookingStatus.ACCEPTED -> "Accepté"
        BookingStatus.REJECTED -> "Refusé"
        BookingStatus.COMPLETED -> "Terminé"
        BookingStatus.CANCELLED -> "Annulé"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    booking.category.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                booking.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
        }
    }
}

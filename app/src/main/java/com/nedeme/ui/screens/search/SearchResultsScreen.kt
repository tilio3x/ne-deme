package com.nedeme.ui.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.nedeme.ui.components.TradespersonCard
import com.nedeme.util.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    uiState: SearchUiState,
    onTradespersonClick: (String) -> Unit,
    onToggleMapView: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.category.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = onToggleMapView) {
                        Icon(
                            if (uiState.showMapView) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (uiState.showMapView) "Liste" else "Carte"
                        )
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
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(uiState.error, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.tradespeople.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Aucun professionnel disponible",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Réessayez plus tard",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            uiState.showMapView -> {
                SearchMapView(
                    uiState = uiState,
                    onTradespersonClick = onTradespersonClick,
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(uiState.tradespeople) { tradesperson ->
                        val distance = uiState.userLocation?.let { userLoc ->
                            tradesperson.location?.let { tpLoc ->
                                LocationHelper.distanceInKm(userLoc, tpLoc)
                            }
                        }
                        TradespersonCard(
                            tradesperson = tradesperson,
                            onClick = { onTradespersonClick(tradesperson.userId) },
                            distanceKm = distance
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchMapView(
    uiState: SearchUiState,
    onTradespersonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Center map on user location or first tradesperson with location
    val defaultLocation = uiState.userLocation?.let {
        LatLng(it.latitude, it.longitude)
    } ?: uiState.tradespeople.firstNotNullOfOrNull { tp ->
        tp.location?.let { LatLng(it.latitude, it.longitude) }
    } ?: LatLng(12.6392, -8.0029) // Bamako as fallback

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        uiState.tradespeople.forEach { tradesperson ->
            tradesperson.location?.let { geoPoint ->
                val position = LatLng(geoPoint.latitude, geoPoint.longitude)
                val snippet = buildString {
                    append("%.1f".format(tradesperson.averageRating))
                    append(" ★")
                    if (tradesperson.hourlyRate != null) {
                        append(" • ${tradesperson.hourlyRate.toInt()} FCFA/h")
                    }
                    if (tradesperson.isFeatured) append(" • Premium")
                }
                Marker(
                    state = MarkerState(position = position),
                    title = tradesperson.displayName,
                    snippet = snippet,
                    onInfoWindowClick = { onTradespersonClick(tradesperson.userId) }
                )
            }
        }
    }
}

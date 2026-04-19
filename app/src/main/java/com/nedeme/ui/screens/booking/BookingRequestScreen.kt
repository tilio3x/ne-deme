package com.nedeme.ui.screens.booking

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.nedeme.util.TestTags
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestScreen(
    uiState: BookingUiState,
    category: String,
    onSubmit: (description: String, date: Date?) -> Unit,
    onNavigateBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE) }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = Date(it)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Annuler") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Demande de service") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isSuccess) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Demande envoyée !",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.testTag(TestTags.BOOKING_SUCCESS)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Le professionnel a été notifié",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Retour")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                Text(
                    text = "Catégorie: ${category.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Décrivez votre besoin") },
                    placeholder = { Text("Ex: Fuite d'eau dans la cuisine...") },
                    modifier = Modifier.fillMaxWidth().height(160.dp).testTag(TestTags.BOOKING_DESCRIPTION),
                    maxLines = 6
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedCard(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = selectedDate?.let { dateFormat.format(it) }
                                ?: "Choisir une date (optionnel)"
                        )
                    }
                }

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(uiState.error, color = MaterialTheme.colorScheme.error)
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { onSubmit(description, selectedDate) },
                    enabled = description.isNotBlank() && !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag(TestTags.BOOKING_SUBMIT)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Envoyer la demande")
                    }
                }
            }
        }
    }
}

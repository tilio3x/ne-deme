package com.nedeme.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nedeme.data.model.Tradesperson
import com.nedeme.util.Resource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TradespersonSetupScreen(
    viewModel: TradespersonSetupViewModel = hiltViewModel(),
    onSetupComplete: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val categories = listOf(
        "plumbing" to "Plomberie",
        "electrical" to "Electricité",
        "gardening" to "Jardinage",
        "cleaning" to "Nettoyage",
        "painting" to "Peinture",
        "carpentry" to "Menuiserie",
        "masonry" to "Maçonnerie",
        "hvac" to "Climatisation"
    )

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Configurer votre profil") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Vos spécialités", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { (id, name) ->
                    FilterChip(
                        selected = id in selectedCategories,
                        onClick = {
                            selectedCategories = if (id in selectedCategories)
                                selectedCategories - id
                            else
                                selectedCategories + id
                        },
                        label = { Text(name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description de vos services") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                label = { Text("Ville") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = hourlyRate,
                onValueChange = { hourlyRate = it },
                label = { Text("Tarif horaire (FCFA) - optionnel") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selectedCategories.isEmpty()) {
                        error = "Sélectionnez au moins une spécialité"
                        return@Button
                    }
                    if (description.isBlank() || city.isBlank()) {
                        error = "Remplissez tous les champs obligatoires"
                        return@Button
                    }
                    scope.launch {
                        isLoading = true
                        viewModel.saveProfile(
                            categories = selectedCategories.toList(),
                            description = description,
                            city = city,
                            hourlyRate = hourlyRate.toDoubleOrNull()
                        ) { success ->
                            isLoading = false
                            if (success) onSetupComplete()
                            else error = "Échec de l'enregistrement"
                        }
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enregistrer")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

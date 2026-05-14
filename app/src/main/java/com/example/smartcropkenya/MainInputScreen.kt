package com.example.smartcropkenya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainInputScreen(
    onNavigateToResults: () -> Unit,
    viewModel: SmartCropViewModel = viewModel()
) {
    val locations by viewModel.locations.collectAsState()
    var errorMessage by remember { mutableStateOf("") }

    var countyExpanded by remember { mutableStateOf(false) }
    val counties = remember(locations) {
        locations.map { it.county }.distinct().sorted()
    }
    var selectedCounty by remember { mutableStateOf("") }

    val subCounties = remember(selectedCounty, locations) {
        locations.filter { it.county == selectedCounty }
    }
    var subCountyExpanded by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf<SubcountyLocation?>(null) }

    LaunchedEffect(selectedCounty) { selectedLocation = null }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SmartCrop Kenya",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            "Soil & Crop Analysis",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Info banner
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Select your location and we'll automatically detect your soil nutrients, " +
                            "pH, and 3-month weather outlook to recommend the best crops.",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Location section
            SectionCard(title = "Location") {
                // County dropdown
                ExposedDropdownMenuBox(
                    expanded = countyExpanded,
                    onExpandedChange = { countyExpanded = !countyExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCounty.ifEmpty { "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("County") },
                        placeholder = { Text("Select county") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = countyExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = countyExpanded,
                        onDismissRequest = { countyExpanded = false }
                    ) {
                        if (counties.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Loading...") },
                                onClick = {}
                            )
                        } else {
                            counties.forEach { county ->
                                DropdownMenuItem(
                                    text = { Text(county) },
                                    onClick = {
                                        selectedCounty = county
                                        countyExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-county dropdown
                ExposedDropdownMenuBox(
                    expanded = subCountyExpanded && selectedCounty.isNotEmpty(),
                    onExpandedChange = {
                        if (selectedCounty.isNotEmpty()) subCountyExpanded = !subCountyExpanded
                    }
                ) {
                    OutlinedTextField(
                        value = selectedLocation?.name ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sub-county") },
                        placeholder = { Text("Select sub-county") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCountyExpanded)
                        },
                        enabled = selectedCounty.isNotEmpty(),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = subCountyExpanded && selectedCounty.isNotEmpty(),
                        onDismissRequest = { subCountyExpanded = false }
                    ) {
                        subCounties.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location.name) },
                                onClick = {
                                    selectedLocation = location
                                    subCountyExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Error message
            if (errorMessage.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Submit button — navigate immediately, ResultsScreen handles loading state
            Button(
                onClick = {
                    val loc = selectedLocation
                    if (loc == null) {
                        errorMessage = "Please select a county and sub-county."
                    } else {
                        errorMessage = ""
                        viewModel.predictByLocation(loc) // fire the API call
                        onNavigateToResults()            // navigate straight away
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Analyse & Predict Crops",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
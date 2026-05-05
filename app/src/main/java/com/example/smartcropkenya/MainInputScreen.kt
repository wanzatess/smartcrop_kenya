package com.example.smartcropkenya

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainInputScreen(
    onNavigateToResults: () -> Unit,
    viewModel: SmartCropViewModel = viewModel()
) {
    val locations by viewModel.locations.collectAsState()
    var nitrogen   by remember { mutableStateOf("") }
    var phosphorus by remember { mutableStateOf("") }
    var potassium  by remember { mutableStateOf("") }
    var ph         by remember { mutableStateOf("") }
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

            // Location section
            SectionCard(title = "Location") {
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = countyExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCountyExpanded) },
                        enabled = selectedCounty.isNotEmpty(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
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

            // Soil nutrients section
            SectionCard(title = "Soil Nutrients (mg/kg)") {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nitrogen,
                        onValueChange = { nitrogen = it },
                        label = { Text("Nitrogen (N)") },
                        placeholder = { Text("Enter value between 1 - 100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = phosphorus,
                        onValueChange = { phosphorus = it },
                        label = { Text("Phosphorus (P)") },
                        placeholder = { Text("Enter value between 1 - 100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = potassium,
                        onValueChange = { potassium = it },
                        label = { Text("Potassium (K)") },
                        placeholder = { Text("Enter value between 1 - 100") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                    )
                }
            }

            // Soil pH section
            SectionCard(title = "Soil pH") {
                OutlinedTextField(
                    value = ph,
                    onValueChange = { ph = it },
                    label = { Text("pH Level") },
                    placeholder = { Text("0.0 — 14.0") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    supportingText = { Text("Optimal range for most crops: 6.0 — 7.5") }
                )
            }

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

            Button(
                onClick = {
                    val n    = nitrogen.toIntOrNull() ?: -1
                    val p    = phosphorus.toIntOrNull() ?: -1
                    val k    = potassium.toIntOrNull() ?: -1
                    val phVal = ph.toDoubleOrNull() ?: -1.0
                    val loc  = selectedLocation
                    when {
                        loc == null -> errorMessage = "Please select a county and sub-county."
                        n !in 1..100 || p !in 1..100 || k !in 1..100 ->
                            errorMessage = "NPK values must each be between 1 and 100 mg/kg."
                        phVal < 0.0 || phVal > 14.0 ->
                            errorMessage = "Please enter a valid pH between 0.0 and 14.0."
                        else -> {
                            errorMessage = ""
                            viewModel.submitData(loc, n, p, k, phVal)
                            onNavigateToResults()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Analyse Soil Data",
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}
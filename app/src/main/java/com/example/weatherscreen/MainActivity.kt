package com.example.weatherscreen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherscreen.data.DatabaseHelper
import com.example.weatherscreen.data.LocationData
import com.example.weatherscreen.ui.theme.WeatherscreenTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherscreenTheme {
                SavedCitiesScreen()
            }
        }
    }
}

@Composable
fun SavedCitiesScreen() {
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)
    val showDialog = remember { mutableStateOf(false) }
    val locations = remember { mutableStateOf(databaseHelper.getAllLocations().toMutableList()) }

    if (showDialog.value) {
        AddCityDialog(
            onDismissRequest = { showDialog.value = false },
            onCityAdded = { newCity, latitude, longitude ->
                databaseHelper.addLocation(newCity, latitude, longitude)
                locations.value = databaseHelper.getAllLocations().toMutableList()
                showDialog.value = false
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Button(
                    onClick = { showDialog.value = true }
                ) {
                    Text(text = "Add City")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Saved Cities",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            SavedCitiesList(locations = locations)
        }
    }
}

@Composable
fun AddCityDialog(
    onDismissRequest: () -> Unit,
    onCityAdded: (String, Double, Double) -> Unit
) {
    val cityName = remember { mutableStateOf("") }
    val latitude = remember { mutableStateOf("") }
    val longitude = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = "Add City") },
        text = {
            Column {
                TextField(
                    value = cityName.value,
                    onValueChange = { cityName.value = it },
                    placeholder = { Text(text = "Enter city name") }
                )
                TextField(
                    value = latitude.value,
                    onValueChange = { latitude.value = it },
                    placeholder = { Text(text = "Enter latitude") }
                )
                TextField(
                    value = longitude.value,
                    onValueChange = { longitude.value = it },
                    placeholder = { Text(text = "Enter longitude") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCityAdded(cityName.value, latitude.value.toDoubleOrNull() ?: 0.0, longitude.value.toDoubleOrNull() ?: 0.0)
                    cityName.value = ""
                    latitude.value = ""
                    longitude.value = ""
                }
            ) {
                Text(text = "Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCitiesList(locations: MutableState<MutableList<LocationData>>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = locations.value, key = { it.id.hashCode() }) { location ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = location.location,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}
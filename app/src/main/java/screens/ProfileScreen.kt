package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.viewmodels.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AutoGreen.network.models.PlantInfo

@Composable
fun ProfileScreen(viewModel: SearchViewModel = viewModel()) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Column (modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material.Text(
                        text = "Profile",
                        style = TextStyle(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    )
                }
            },
            backgroundColor = Color(0xFF008425),
            contentColor = Color.White,
            modifier = Modifier.height(80.dp)
        )


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) }, // Updates the query
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(12.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (searchQuery.isEmpty()) {
                        Text("Search for a plant...", color = Color.Gray)
                    }
                    innerTextField()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Results
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (searchResults.isEmpty() && searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = "No results found.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            } else {
                items(searchResults) { plant ->
                    PlantItem(plant)
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: PlantInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color(0xFFDDFFDD), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text = "Name: ${plant.speciesName}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = "Temperature Range: ${plant.minTempRange}°C - ${plant.maxTempRange}°C")
        Text(text = "Moisture Level: ${plant.soilMoistureLevel}")
    }
}
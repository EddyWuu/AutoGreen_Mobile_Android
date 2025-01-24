package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.viewmodels.SearchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.AutoGreen.network.models.PlantInfo
import com.example.AutoGreen.network.LearningModeManager
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(viewModel: SearchViewModel = viewModel()) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    // state to remember plant selected for when the pop up comes up
    var selectedPlant by remember { mutableStateOf<PlantInfo?>(null) }

    // initial fetch of all plants to show for no queries select
    val coroutineScope = rememberCoroutineScope()
    remember {
        coroutineScope.launch {
            viewModel.fetchAllPlants()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFE9E2)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TOP BAR: TITLE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF304B43))
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material.Text(
                text = "Profile",
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) }, // updates the query
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

        // search results
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
                    PlantItem(plant) {
                        selectedPlant = plant
                    }
                }
            }
        }

        if (selectedPlant != null) {
            PlantDetailsDialog(
                plant = selectedPlant!!,
                onDismiss = { selectedPlant = null },
                viewModel = viewModel
            )
        }
    }
}

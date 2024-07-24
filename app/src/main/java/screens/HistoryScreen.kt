package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.viewmodels.HistoryViewModel
import kotlinx.coroutines.delay

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    // Observe the data from the ViewModel
    val temperatureData by viewModel.temperatureData.observeAsState(emptyList())
    val humidityData by viewModel.humidityData.observeAsState(emptyList())
    val soilMoistureData by viewModel.soilMoistureData.observeAsState(emptyList())
    val waterTankData by viewModel.waterTankData.observeAsState(emptyList())

    // Fetch sensor data every 10 seconds
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchSensorHistory(deviceId = 1)
            delay(5000L)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "History",
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
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFF91FF87))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HistoryCard(title = "Humidity", dataList = humidityData)
                HistoryCard(title = "Temperature", dataList = temperatureData)
                HistoryCard(title = "Soil Moisture", dataList = soilMoistureData)
                HistoryCard(title = "Water Tank Level", dataList = waterTankData)
            }
        }
    }
}

@Composable
fun HistoryCard(title: String, dataList: List<Float>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer {
                shadowElevation = 8.dp.toPx()
                shape = RoundedCornerShape(16.dp)
                clip = false
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false,
                ambientColor = Color.Black,
                spotColor = Color.Black
            ),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF008425), Color(0xFF6AA47A))
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = dataList.joinToString(separator = ", "),
                fontSize = 18.sp,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

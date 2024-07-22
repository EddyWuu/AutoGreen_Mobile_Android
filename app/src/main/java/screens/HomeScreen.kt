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
import com.example.AutoGreen.viewmodels.HomeViewModel
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    // Observe the data from the ViewModel
    val temperatureData by viewModel.temperatureData.observeAsState(emptyList())
    val humidityData by viewModel.humidityData.observeAsState(emptyList())
    val soilMoistureData by viewModel.soilMoistureData.observeAsState(emptyList())
    val waterTankData by viewModel.waterTankData.observeAsState(emptyList())


    // call fetchTemperature() every 10 seconds and get sensor data
    LaunchedEffect(Unit) {
        while (true) {

            viewModel.fetchSensorData(deviceId = 1)

            // 10 sec delay
            delay(10000L)
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
                    text = "Home",
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
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (humidityData.isNotEmpty()) {
                        // send latest data from our humidity list
                        val latestHumidity = humidityData.last()
                        InfoCard(title = "Humidity", data = "$latestHumidity%")
                    } else {
                        InfoCard(title = "Humidity", data = "N/A")
                    }

                    if (temperatureData.isNotEmpty()) {
                        val latestTemperature = temperatureData.last()
                        InfoCard(title = "Temperature", data = "$latestTemperature°C")
                    } else {
                        InfoCard(title = "Temperature", data = "N/A")
                    }

                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (soilMoistureData.isNotEmpty()) {
                        val latestSoilMoisture = soilMoistureData.last()
                        InfoCard(title = "Soil Moisture", data = "$latestSoilMoisture%")
                    } else {
                        InfoCard(title = "Soil Moisture", data = "N/A")
                    }

                    if (waterTankData.isNotEmpty()) {
                        val latestWaterTankLevel = waterTankData.last()
                        InfoCard(title = "Water Tank Level", data = "$latestWaterTankLevel%")
                    } else {
                        InfoCard(title = "Water Tank Level", data = "N/A")
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(title: String, data: String) {
    Card(
        modifier = Modifier
            .size(150.dp)
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
                .fillMaxSize()
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
                text = data,
                fontSize = 26.sp,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import android.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.roundToInt

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    // Observe the data from the ViewModel
    val temperatureData by viewModel.temperatureData.observeAsState(emptyList())
    val humidityData by viewModel.humidityData.observeAsState(emptyList())
    val soilMoistureData by viewModel.soilMoistureData.observeAsState(emptyList())
    val waterTankData by viewModel.waterTankData.observeAsState(emptyList())

    val sampleHumidityData = listOf(
        65.2f, 64.8f, 66.1f, 65.7f, 63.9f, 64.5f, 65.8f, 67.2f, 66.4f, 65.9f,
        64.7f, 65.3f, 66.8f, 65.5f, 64.2f, 65.1f, 66.3f, 67.0f, 66.1f, 65.4f
    )

    val sampleTemperatureData = listOf(
        23.5f, 23.8f, 23.2f, 24.1f, 23.7f, 23.9f, 24.3f, 24.0f, 23.6f, 23.8f,
        23.4f, 23.9f, 24.2f, 23.8f, 23.5f, 24.0f, 24.4f, 24.1f, 23.7f, 23.9f
    )

    val sampleSoilMoistureData = listOf(
        45.5f, 44.8f, 46.2f, 45.7f, 44.9f, 45.8f, 46.5f, 45.2f, 44.7f, 45.9f,
        45.3f, 46.1f, 45.8f, 44.6f, 45.4f, 46.3f, 45.9f, 45.1f, 45.7f, 46.4f
    )


    // Fetch sensor data every 10 seconds
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.fetchSensorHistory(deviceId = 2)
            delay(5000L)
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
            Text(
                text = "History",
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
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SensorDataGraph(
                    title = "Humidity",
//                    dataPoints = humidityData.map { it.toFloat() },
                    dataPoints = sampleHumidityData,
                    graphColor = Color(0xFF2196F3)
                )

                SensorDataGraph(
                    title = "Temperature",
//                    dataPoints = temperatureData.map { it.toFloat() },
                    dataPoints = sampleTemperatureData,
                    graphColor = Color(0xFFF44336)
                )

                SensorDataGraph(
                    title = "Soil Moisture",
//                    dataPoints = soilMoistureData.map { it.toFloat() },
                    dataPoints = sampleSoilMoistureData,
                    graphColor = Color(0xFF4CAF50)
                )

            }
        }
    }
}

@Composable
fun SensorDataGraph(
    title: String,
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    graphColor: Color = Color(0xFF4CAF50),
    backgroundColor: Color = Color(0xFF304B43)
) {
    var selectedPoint by remember { mutableStateOf<Pair<Int, Float>?>(null) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(215.dp)
            .padding(vertical = 8.dp),
        elevation = 8.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                )

                selectedPoint?.let { (index, value) ->
                    Text(
                        text = "Point ${index + 1}: $value",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }

            if (dataPoints.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    val maxValue = dataPoints.maxOrNull() ?: 0f
                                    val minValue = dataPoints.minOrNull() ?: 0f
                                    val range = (maxValue - minValue).coerceAtLeast(1f)
                                    val spaceBetweenPoints = size.width / (dataPoints.size - 1)

                                    val index = (offset.x / spaceBetweenPoints).roundToInt()
                                        .coerceIn(0, dataPoints.size - 1)
                                    selectedPoint = index to dataPoints[index]
                                }
                            }
                    ) {
                        val maxValue = dataPoints.maxOrNull() ?: 0f
                        val minValue = dataPoints.minOrNull() ?: 0f
                        val range = (maxValue - minValue).coerceAtLeast(1f)

                        // Draw grid lines
                        val gridLines = 5
                        val textPaint = Paint().apply {
                            color = android.graphics.Color.WHITE
                            alpha = 128
                            textSize = 8.sp.toPx()
                            textAlign = Paint.Align.LEFT
                        }

                        for (i in 0..gridLines) {
                            val y = size.height * i / gridLines
                            drawLine(
                                color = Color.White.copy(alpha = 0.2f),
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = 0.5.dp.toPx()
                            )

                            val gridValue = minValue + (range * (gridLines - i) / gridLines)
                            drawContext.canvas.nativeCanvas.drawText(
                                "%.1f".format(gridValue),
                                0f,
                                y,
                                textPaint
                            )
                        }

                        val path = Path()
                        val spaceBetweenPoints = size.width / (dataPoints.size - 1)

                        dataPoints.forEachIndexed { index, value ->
                            val x = index * spaceBetweenPoints
                            val y = size.height - ((value - minValue) / range * size.height)

                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }

                            val isSelected = selectedPoint?.first == index
                            drawCircle(
                                color = if (isSelected) Color.White else graphColor,
                                radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }

                        drawPath(
                            path = path,
                            color = graphColor,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available",
                        color = Color.White
                    )
                }
            }

            if (dataPoints.isNotEmpty()) {
                Text(
                    text = "Latest: ${dataPoints.last()}",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
        }
    }
}
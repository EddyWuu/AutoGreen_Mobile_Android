package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.network.viewmodels.ControlsViewModel

@Composable
fun ControlsScreen(viewModel: ControlsViewModel) {

    var showManualDialog by remember { mutableStateOf(false) }
    var showAutomaticDialog by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false)}

    var waterAmount by remember { mutableStateOf("") }
    var automaticWateringInterval by remember { mutableStateOf("") }
    var automaticWaterAmount by remember { mutableStateOf("")}
    var tempValue by remember { mutableStateOf("")}

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
                        text = "Controls",
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


        // screen contents
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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GradientButton(
                    text = "Set Water Amount",
                    onClick = { showManualDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Set Automatic Watering",
                    onClick = { showAutomaticDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                GradientButton(
                    text = "Set Temperature",
                    onClick = { showTemperatureDialog = true }
                )
            }




            // logic
            // manual watering button clicked
            if (showManualDialog) {
                AlertDialog(
                    onDismissRequest = { showManualDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = "Enter Water Amount",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                        )
                    )},
                    text = {
                        Column {
                            Text(text = "Amount of water (ml):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            TextField(
                                value = waterAmount,
                                onValueChange = { waterAmount = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GradientButton(text = "Cancel") {
                                showManualDialog = false
                            }
                            GradientButton(text = "Confirm") {
                                viewModel.sendManualWaterAPI(deviceId = 1, waterAmount = waterAmount.toIntOrNull() ?: 0)
                                showManualDialog = false
                            }
                        }
                    }
                )
            }

            // automatic watering dialog button clicked
            if (showAutomaticDialog) {
                AlertDialog(
                    onDismissRequest = { showAutomaticDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = "Set Automatic Watering Interval",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    },
                    text = {
                        Column {
                            Text(
                                text = "Watering interval (minutes):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            TextField(
                                value = automaticWateringInterval,
                                onValueChange = { automaticWateringInterval = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Amount of water (ml):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            TextField(
                                value = automaticWaterAmount,
                                onValueChange = { automaticWaterAmount = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GradientButton(text = "Cancel") {
                                showAutomaticDialog = false
                            }
                            GradientButton(text = "Confirm") {
                                viewModel.sendAutomaticWaterAPI(deviceId = 1, automaticWaterAmount = automaticWaterAmount.toIntOrNull() ?: 0, timeInterval = automaticWateringInterval.toIntOrNull() ?: 0)
                                showAutomaticDialog = false
                            }
                        }
                    }
                )
            }

            // set temperature button clicked
            if (showTemperatureDialog) {
                AlertDialog(
                    onDismissRequest = { showTemperatureDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = "Enter optimal temperature",
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )},
                    text = {
                        Column {
                            Text(text = "Temperature(°C):",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                            TextField(
                                value = tempValue,
                                onValueChange = { tempValue = it },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            GradientButton(text = "Cancel") {
                                showTemperatureDialog = false
                            }
                            GradientButton(text = "Confirm") {
                                viewModel.sendTemperatureAPI(deviceId = 1, setTemperature = tempValue.toIntOrNull() ?: 0)
                                showTemperatureDialog = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF008425), Color(0xFF5DA06F))
                    ),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 30.dp, vertical = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                style = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            )
        }
    }
}
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.network.viewmodels.ControlsViewModel

@Composable
fun ControlsScreen(viewModel: ControlsViewModel) {

    var showManualDialog by remember { mutableStateOf(false) }
    var showAutomaticDialog by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false)}
    var showMessageDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    var waterAmount by remember { mutableStateOf("") }
    var automaticWateringInterval by remember { mutableStateOf("") }
    var automaticWaterAmount by remember { mutableStateOf("")}
    var tempValue by remember { mutableStateOf("")}

    var errorMessage by remember { mutableStateOf("") }
    var errorMessage2 by remember { mutableStateOf("") }

    val snackbarMessage by viewModel.snackbarMessage.observeAsState()


    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            dialogMessage = it
            showMessageDialog = true
            viewModel.snackbarMessage.postValue(null)
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
                    onDismissRequest = {
                        showManualDialog = false
                        waterAmount = ""
                    },
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
                                onValueChange = {
                                    val filteredInput = it.filter { char -> char.isDigit() }
                                    waterAmount = filteredInput
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
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
                                waterAmount = ""
                                errorMessage = ""
                            }
                            GradientButton(text = "Confirm") {
                                val number = waterAmount.toIntOrNull()
                                when {
                                    number == null -> {
                                        errorMessage = "Please enter a valid number."
                                    }
                                    number > 500 -> {
                                        errorMessage = "The value cannot exceed 500 ml."
                                    }
                                    number < 100 -> {
                                        errorMessage2 = "The value cannot be lower than 100 ml."
                                    }
                                    else -> {
                                        viewModel.sendManualWaterAPI(deviceId = 1, waterAmount = number)
                                        showManualDialog = false
                                        waterAmount = ""
                                        errorMessage = ""
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // automatic watering dialog button clicked
            if (showAutomaticDialog) {

                var selectedUnit by remember { mutableStateOf("Days") }

                AlertDialog(
                    onDismissRequest = {
                        showAutomaticDialog = false
                        automaticWateringInterval = ""
                        automaticWaterAmount = ""
                    },
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
                            // intervals
                            Text(
                                text = "Watering interval:",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = automaticWateringInterval,
                                    onValueChange = { input ->
                                        automaticWateringInterval = input.filter { it.isDigit() }
                                    },
                                    modifier = Modifier.weight(1f),
                                    textStyle = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp
                                    ),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                // dropdown for unit select
                                var expanded by remember { mutableStateOf(false) }

                                Box(
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Button(
                                        onClick = { expanded = true },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = Color(0xFF008425),
                                            contentColor = Color.White
                                        )) {
                                        Text(text = selectedUnit)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        listOf("Minutes", "Hours", "Days", "Weeks").forEach { unit ->
                                            DropdownMenuItem(onClick = {
                                                selectedUnit = unit
                                                expanded = false
                                            }) {
                                                Text(text = unit)
                                            }
                                        }
                                    }
                                }
                            }

                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

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
                                onValueChange = { input ->
                                    automaticWaterAmount = input.filter { it.isDigit() }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            if (errorMessage2.isNotEmpty()) {
                                Text(
                                    text = errorMessage2,
                                    color = Color.Red,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
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
                                automaticWateringInterval = ""
                                automaticWaterAmount = ""
                                errorMessage = ""
                                errorMessage2 = ""
                            }
                            GradientButton(text = "Confirm") {
                                val interval = automaticWateringInterval.toIntOrNull()
                                val amount = automaticWaterAmount.toIntOrNull()

                                when {
                                    interval == null -> {
                                        errorMessage = "Please enter a valid number for interval."
                                    }
                                    amount == null -> {
                                        errorMessage2 = "Please enter a valid number for amount."
                                    }
                                    amount > 500 -> {
                                        errorMessage2 = "The value cannot exceed 500 ml."
                                    }
                                    amount < 100 -> {
                                        errorMessage2 = "The value cannot be lower than 100 ml."
                                    }
                                    else -> {
                                        // conversions, default is min
                                        val intervalInMinutes = when (selectedUnit) {
                                            "Minutes" -> interval
                                            "Hours" -> interval * 60
                                            "Days" -> interval * 60 * 24
                                            "Weeks" -> interval * 60 * 24 * 7
                                            else -> interval
                                        }

                                        viewModel.sendAutomaticWaterAPI(
                                            deviceId = 1,
                                            automaticWaterAmount = amount,
                                            timeInterval = intervalInMinutes
                                        )

                                        showAutomaticDialog = false
                                        errorMessage = ""
                                        errorMessage2 = ""
                                        automaticWateringInterval = ""
                                        automaticWaterAmount = ""
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // set temperature button clicked
            if (showTemperatureDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showTemperatureDialog = false
                        tempValue = ""
                    },
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
                                onValueChange = {
                                    val filteredInput = it.filter { char -> char.isDigit() }
                                    tempValue = filteredInput
                                },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 16.sp
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            if (errorMessage.isNotEmpty()) {
                                Text(
                                    text = errorMessage,
                                    color = Color.Red,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
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
                                errorMessage = ""
                                tempValue = ""
                            }
                            GradientButton(text = "Confirm") {
                                val number = tempValue.toIntOrNull()
                                when {
                                    number == null -> {
                                        errorMessage = "Please enter a valid number."
                                    }
                                    number > 50 -> {
                                        errorMessage = "The value cannot exceed 50°C."
                                    }
                                    number < 0 -> {
                                        errorMessage = "Negative values are not allowed."
                                    }
                                    else -> {
                                        viewModel.sendTemperatureAPI(deviceId = 1, setTemperature = tempValue.toIntOrNull() ?: 0)
                                        showTemperatureDialog = false
                                        errorMessage = ""
                                        tempValue = ""
                                    }
                                }
                            }
                        }
                    }
                )
            }

            // request succ or fail dialog
            if (showMessageDialog) {
                AlertDialog(
                    onDismissRequest = { showMessageDialog = false },
                    shape = RoundedCornerShape(16.dp),
                    title = {
                        Text(
                            text = "            Request Status",
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    },
                    text = {
                        Text(
                            text = dialogMessage,
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    },
                    buttons = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            GradientButton(text = "OK") {
                                showMessageDialog = false
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
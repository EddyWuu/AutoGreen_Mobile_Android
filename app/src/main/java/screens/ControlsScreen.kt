package screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.AutoGreen.network.LearningModeManager
import com.example.AutoGreen.network.viewmodels.ControlsViewModel
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import com.example.AutoGreen.viewmodels.SearchViewModel
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ControlsScreen(viewModel: ControlsViewModel, onSheetVisibilityChanged: (Boolean) -> Unit) {

    var showManualDialog by remember { mutableStateOf(false) }
    var showAutomaticDialog by remember { mutableStateOf(false) }
    var showTemperatureDialog by remember { mutableStateOf(false)}
    var showMessageDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    var showLearningModeWarning by remember { mutableStateOf(false) }
    var pendingAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    var selectedMode by remember { mutableStateOf("Preset") }

    var waterAmount by remember { mutableStateOf("") }
    var automaticWateringInterval by remember { mutableStateOf("") }
    var automaticWaterAmount by remember { mutableStateOf("")}
    var tempValue by remember { mutableStateOf("")}
    var selectedUnit by remember { mutableStateOf("Days") }
    val setTempValue by viewModel.setTempValue.observeAsState("")

    val error by viewModel.errorMessage.observeAsState("")
    val error2 by viewModel.errorMessage2.observeAsState("")

    val snackbarMessage by viewModel.snackbarMessage.observeAsState()
    val isLearningMode by LearningModeManager.isLearning.collectAsState()

    val deviceStatus by viewModel.deviceStatus.observeAsState()


    // bottom sheet stuff for serach
    val searchViewModel = remember { SearchViewModel() } // Instantiate here
    var showSearchSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isLearningMode) {
        Log.d("ControlsScreen", "isLearningMode: $isLearningMode")
    }


    LaunchedEffect(deviceStatus) {

        viewModel.fetchSetTemperature()
        viewModel.fetchDeviceStatus(2)

    }
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            dialogMessage = it
            showMessageDialog = true
            viewModel.snackbarMessage.postValue(null)
        }
    }

    // sheet visibility
    LaunchedEffect(sheetState.isVisible) {
        onSheetVisibilityChanged(sheetState.isVisible)
    }

    ModalBottomSheetLayout(
        sheetContent = {
            ProfileScreenContent(
                onClose = {
                    coroutineScope.launch {
                        sheetState.hide()
                    }
                },
                viewModel = searchViewModel // passing in search vm
            )
        },
        sheetState = sheetState // using bottom sheet state
    ) {

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
                    text = "Controls",
                    color = Color.White,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // OPTIONS: PRESET / LEARNING MODE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Preset option
                Button(
                    onClick = {
                        if (selectedMode == "Learning") {
                            selectedMode = "Preset"
                            //                    LearningModeManager.setLearningMode(!isLearningMode)
                            // TODO: need to toggle learning mode off only, turning on needs to go through search
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selectedMode == "Preset") Color(0xFF304B43) else Color(0xFFCCCCCC)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedMode == "Preset") Color(0xFF304B43) else Color(0xFFCCCCCC),
                        contentColor = if (selectedMode == "Preset") Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = "Preset",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Learning mode option
                Button(
                    onClick = {
                        if (selectedMode == "Preset") {
                            selectedMode = "Learning"
                            //                    LearningModeManager.setLearningMode(!isLearningMode)
                            // TODO: need to toggle learning mode off only, turning on needs to go through search
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (selectedMode == "Learning") Color(0xFF304B43) else Color(0xFFCCCCCC)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedMode == "Learning") Color(0xFF304B43) else Color(0xFFCCCCCC),
                        contentColor = if (selectedMode == "Learning") Color.White else Color.Black
                    )
                ) {
                    Text(
                        text = "Learning Mode",
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            if (selectedMode == "Preset") {
                // screen contents
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(0.7f)
                        .padding(0.dp),

                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { showManualDialog = true }
                                .padding(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "Set Water Amount",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ranges from 100 ml to 500 ml",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Navigate",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF304B43)
                                )
                            }
                        }


                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { showAutomaticDialog = true }
                                .padding(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Column {
                                    Text(
                                        text = "Automatic Watering",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (isLearningMode) {
                                            "NA"
                                        } else {
                                            deviceStatus?.let { status ->
                                                "Every ${viewModel.formatTimeInterval(status.watering_frequency)}, Amount: ${status.watering_amount} ml"
                                            } ?: "Not set"
                                        },
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )

                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Navigate",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF304B43)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable { showTemperatureDialog = true }
                                .padding(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "Temperature",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 24.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ranges from 0°C to 50°C",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 12.sp,
                                            color = Color(0xFF304B43)
                                        )
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(50.dp)
                                        .background(Color(0xFF304B43))
                                )
                                Text(
                                    text = "$setTempValue°C",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color(0xFF304B43)
                                    )
                                )

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Navigate",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color(0xFF304B43)
                                )
                            }
                        }
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
                            backgroundColor = Color(0xFFEFE9E2),
                            title = {
                                Text(
                                    text = "Enter Water Amount",
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
                                        text = "Amount of water (ml):",
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
                                    if (error.isNotEmpty()) {
                                        Text(
                                            text = error,
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
                                    GradientButton(
                                        text = "Cancel",
                                        buttonColor = Color.Black,
                                        onClick = {
                                            showManualDialog = false
                                            waterAmount = ""
                                            viewModel.errorMessage.value = ""
                                        }
                                    )
                                    GradientButton(
                                        text = "Confirm",
                                        buttonColor = Color(0xFF304B43),
                                        onClick = {
                                            if (viewModel.validateManualWaterAmount(waterAmount)) {
                                                if (isLearningMode) {
                                                    // Show learning mode warning first
                                                    showLearningModeWarning = true
                                                    pendingAction = {
                                                        viewModel.sendManualWaterAPI(
                                                            deviceId = 2,
                                                            waterAmount = waterAmount.toInt()
                                                        )
                                                        showManualDialog = false
                                                        waterAmount = ""
                                                    }
                                                } else {
                                                    // Directly send the manual watering request
                                                    viewModel.sendManualWaterAPI(
                                                        deviceId = 2,
                                                        waterAmount = waterAmount.toInt()
                                                    )
                                                    showManualDialog = false
                                                    waterAmount = ""
                                                }
                                            }
                                        }
                                    )

                                }
                            }
                        )
                    }

                    // automatic watering dialog button clicked
                    if (showAutomaticDialog) {

//                        var selectedUnit by remember { mutableStateOf("Days") }

                        AlertDialog(
                            onDismissRequest = {
                                showAutomaticDialog = false
                                automaticWateringInterval = ""
                                automaticWaterAmount = ""
                            },
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color(0xFFEFE9E2),
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
                                                automaticWateringInterval =
                                                    input.filter { it.isDigit() }
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
                                                    backgroundColor = Color(0xFF304B43),
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Text(text = selectedUnit)
                                            }
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(Color(0xFFEFE9E2)),
                                            ) {
                                                listOf(
                                                    "Minutes",
                                                    "Hours",
                                                    "Days",
                                                    "Weeks"
                                                ).forEach { unit ->
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

                                    if (error.isNotEmpty()) {
                                        Text(
                                            text = error,
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

                                    if (error2.isNotEmpty()) {
                                        Text(
                                            text = error2,
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
                                    GradientButton(
                                        text = "Cancel",
                                        buttonColor = Color.Black,
                                        onClick = {
                                            showAutomaticDialog = false
                                            automaticWateringInterval = ""
                                            automaticWaterAmount = ""
                                            viewModel.errorMessage.value = ""
                                            viewModel.errorMessage2.value = ""
                                        }
                                    )
                                    GradientButton(
                                        text = "Confirm",
                                        buttonColor = Color(0xFF304B43),
                                        onClick = {
                                            if (viewModel.validateAutomaticWatering(automaticWateringInterval, automaticWaterAmount)) {
                                                if (isLearningMode) {
                                                    showLearningModeWarning = true
                                                    pendingAction = {
                                                        val intervalInMinutes =
                                                            viewModel.convertIntervalToMinutes(
                                                                automaticWateringInterval.toInt(),
                                                                selectedUnit
                                                            )
                                                        viewModel.sendAutomaticWaterAPI(
                                                            deviceId = 2,
                                                            automaticWaterAmount = automaticWaterAmount.toInt(),
                                                            timeInterval = intervalInMinutes
                                                        )
                                                        viewModel.fetchDeviceStatus(2)
                                                        showAutomaticDialog = false
                                                        automaticWateringInterval = ""
                                                        automaticWaterAmount = ""
                                                    }
                                                } else {
                                                    val intervalInMinutes =
                                                        viewModel.convertIntervalToMinutes(
                                                            automaticWateringInterval.toInt(),
                                                            selectedUnit
                                                        )
                                                    viewModel.sendAutomaticWaterAPI(
                                                        deviceId = 2,
                                                        automaticWaterAmount = automaticWaterAmount.toInt(),
                                                        timeInterval = intervalInMinutes
                                                    )
                                                    viewModel.fetchDeviceStatus(2)
                                                    showAutomaticDialog = false
                                                    automaticWateringInterval = ""
                                                    automaticWaterAmount = ""
                                                }
                                            }
                                        }
                                    )

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
                            backgroundColor = Color(0xFFEFE9E2),
                            title = {
                                Text(
                                    text = "Enter optimal temperature",
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
                                        text = "Temperature(°C):",
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
                                    if (error.isNotEmpty()) {
                                        Text(
                                            text = error,
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
                                    GradientButton(
                                        text = "Cancel",
                                        buttonColor = Color.Black,
                                        onClick = {
                                            showTemperatureDialog = false
                                            viewModel.errorMessage.value = ""
                                            tempValue = ""
                                        }
                                    )
                                    GradientButton(
                                        text = "Confirm",
                                        buttonColor = Color(0xFF304B43),
                                        onClick = {
                                            if (viewModel.validateTemperature(tempValue)) {
                                                viewModel.sendTemperatureAPI(
                                                    deviceId = 2,
                                                    setTemperature = tempValue.toInt()
                                                )
                                                showTemperatureDialog = false
                                                tempValue = ""
                                            }
                                        }
                                    )
                                }
                            }
                        )
                    }

                    if (showLearningModeWarning) {
                        AlertDialog(
                            onDismissRequest = { showLearningModeWarning = false },
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color(0xFFEFE9E2),
                            title = {
                                Text(
                                    text = "Warning: Learning Mode Active",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                            },
                            text = {
                                Text(
                                    text = "Sending this request will disable Learning Mode. Do you want to proceed?",
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
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    GradientButton(
                                        text = "Cancel",
                                        buttonColor = Color.Black,
                                        onClick = { showLearningModeWarning = false }
                                    )
                                    GradientButton(
                                        text = "Confirm",
                                        buttonColor = Color(0xFF304B43),
                                        onClick = {
                                            LearningModeManager.setLearningMode(false)
                                            showLearningModeWarning = false
                                            pendingAction?.invoke()
                                        }
                                    )
                                }
                            }
                        )
                    }

                    // request succ or fail dialog
                    if (showMessageDialog) {
                        AlertDialog(
                            onDismissRequest = { showMessageDialog = false },
                            shape = RoundedCornerShape(16.dp),
                            backgroundColor = Color(0xFFEFE9E2),
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
                                    GradientButton(
                                        text = "OK",
                                        onClick = { showMessageDialog = false },
                                        buttonColor = Color(0xFF304B43)
                                    )
                                }
                            }
                        )
                    }
                }
            } else {
                // Learning Mode: COntent
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(start = 10.dp, top = 35.dp, end = 10.dp, bottom = 20.dp),

                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .clickable {
                                    coroutineScope.launch {
                                        sheetState.show()
                                    }
                                }
                                .padding(26.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Search... Filler",
                                style = TextStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF304B43)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    buttonColor: Color = Color(0xFF008425)
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(50)
    ) {
        Box(
            modifier = Modifier
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

@Composable
fun ProfileScreenContent(
    onClose: () -> Unit,
    viewModel: SearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth()
            .background(Color(0xFFEFE9E2))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // top bar of the profilescreen
        Text(
            text = "Search for a Plant",
            style = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF304B43)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // content
        SearchScreen(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // close
        Button(onClick = onClose) {
            Text("Close")
        }
    }
}



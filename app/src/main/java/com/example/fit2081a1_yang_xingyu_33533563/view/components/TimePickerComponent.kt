package com.example.fit2081a1_yang_xingyu_33533563.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    text: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    var hourText by remember { mutableStateOf("") }
    var minuteText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Initialize time from prop
    LaunchedEffect(initialTime) {
        if (initialTime.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
            val parts = initialTime.split(":")
            hourText = parts[0].padStart(2, '0')
            minuteText = parts[1].padStart(2, '0')
        } else {
            hourText = ""
            minuteText = ""
        }
    }
    
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = hourText,
                onValueChange = { newValue ->
                    // Allow only up to 2 digits and validate range
                    val filtered = newValue.filter { it.isDigit() }.take(2)
                    if (filtered.isEmpty()) {
                        hourText = ""
                        errorMessage = null
                    } else {
                        val intVal = filtered.toIntOrNull()
                        if (intVal != null && intVal in 0..23) {
                            hourText = filtered.padStart(2, '0') // Pad with leading zeros
                            errorMessage = null
                        } else {
                            errorMessage = "Hour must be between 0-23"
                            if (filtered.length <= 2) {
                                hourText = filtered // Still allow typing but show error
                            }
                        }
                    }
                    
                    if (hourText.isNotEmpty() && minuteText.isNotEmpty()) {
                        validateAndUpdateTime(hourText, minuteText, onTimeSelected, errorMessage)
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Hour") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("HH", textAlign = TextAlign.Center) },
                isError = errorMessage != null && errorMessage!!.contains("Hour"),
                singleLine = true
            )
            
            Text(":", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 4.dp))
            
            OutlinedTextField(
                value = minuteText,
                onValueChange = { newValue -> 
                    // Allow only up to 2 digits and validate range
                    val filtered = newValue.filter { it.isDigit() }.take(2)
                    if (filtered.isEmpty()) {
                        minuteText = ""
                        errorMessage = null
                    } else {
                        val intVal = filtered.toIntOrNull()
                        if (intVal != null && intVal in 0..59) {
                            minuteText = filtered.padStart(2, '0') // Pad with leading zeros
                            errorMessage = null
                        } else {
                            errorMessage = "Minute must be between 0-59"
                            if (filtered.length <= 2) {
                                minuteText = filtered // Still allow typing but show error
                            }
                        }
                    }
                    
                    if (hourText.isNotEmpty() && minuteText.isNotEmpty()) {
                        validateAndUpdateTime(hourText, minuteText, onTimeSelected, errorMessage)
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Minute") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("MM", textAlign = TextAlign.Center) },
                isError = errorMessage != null && errorMessage!!.contains("Minute"),
                singleLine = true
            )
        }
        
        // Error message
        errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

private fun validateAndUpdateTime(hourText: String, minuteText: String, onTimeSelected: (String) -> Unit, errorMessage: String?) {
    // Only update if there are no validation errors
    if (errorMessage == null) {
        try {
            val hour = hourText.toInt()
            val minute = minuteText.toInt()
            
            if (hour in 0..23 && minute in 0..59) {
                val formattedTime = String.format("%02d:%02d", hour, minute)
                
                // Validate using LocalTime to ensure it's a proper time
                try {
                    LocalTime.parse(formattedTime, DateTimeFormatter.ofPattern("HH:mm"))
                    onTimeSelected(formattedTime)
                } catch (e: DateTimeParseException) {
                    // This shouldn't happen given our prior validation, but just in case
                    e.printStackTrace()
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }
}


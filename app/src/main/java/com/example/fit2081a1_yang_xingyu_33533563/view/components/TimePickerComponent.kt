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

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    text: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    var hourText by remember { mutableStateOf("") }
    var minuteText by remember { mutableStateOf("") }

    LaunchedEffect(initialTime) {
        if (initialTime.matches(Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$"))) {
            val parts = initialTime.split(":")
            hourText = parts[0]
            minuteText = parts[1]
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
                    val num = newValue.take(2).filter { it.isDigit() }
                    if (num.isEmpty()) {
                        hourText = ""
                    } else {
                        val intVal = num.toIntOrNull()
                        if (intVal != null && intVal >= 0 && intVal <= 23) {
                            hourText = num
                        } else if (num.length == 1 && "012".contains(num)) {
                            hourText = num
                        } else if (num.length == 2 && intVal == null) {
                            hourText = num.first().toString()
                        }
                    }
                    updateTime(hourText, minuteText, onTimeSelected)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Hour (00-23)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("HH", textAlign = TextAlign.Center) }
            )
            
            Text(":", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 4.dp))
            
            OutlinedTextField(
                value = minuteText,
                onValueChange = { newValue -> 
                    val num = newValue.take(2).filter { it.isDigit() }
                     if (num.isEmpty()) {
                        minuteText = ""
                    } else {
                        val intVal = num.toIntOrNull()
                        if (intVal != null && intVal >= 0 && intVal <= 59) {
                            minuteText = num
                        } else if (num.length == 1 && "012345".contains(num)) {
                           minuteText = num
                        } else if (num.length == 2 && intVal == null) {
                            minuteText = num.first().toString()
                        }
                    }
                    updateTime(hourText, minuteText, onTimeSelected)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Min (00-59)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("MM", textAlign = TextAlign.Center) }
            )
        }
    }
}

private fun updateTime(hourText: String, minuteText: String, onTimeSelected: (String) -> Unit) {
    val hour = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()

    if (hour != null && hour in 0..23 && minute != null && minute in 0..59) {
        val formattedTime = String.format("%02d:%02d", hour, minute)
        onTimeSelected(formattedTime)
    } else {
        if (hourText.isNotBlank() && minuteText.isNotBlank()) {
            try {
                val h = hourText.toInt()
                val m = minuteText.toInt()
                if (h in 0..23 && m in 0..59) {
                    onTimeSelected(String.format("%02d:%02d", h, m))
                } else {
                    onTimeSelected("$hourText:$minuteText")
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                onTimeSelected("$hourText:$minuteText")
            }
        } else if (hourText.isBlank() && minuteText.isBlank()){
            onTimeSelected("")
        } else {
            onTimeSelected("$hourText:$minuteText")
        }
    }
}


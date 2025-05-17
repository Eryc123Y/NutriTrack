package com.example.fit2081a1_yang_xingyu_33533563.view.components

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimePickerComponent(
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    val mTime = remember { mutableStateOf(initialTime) }
    val mContext = LocalContext.current

    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)

    val mTimePickerDialog = TimePickerDialog(
        mContext,
        { _, hour: Int, minute: Int ->
            // Format time with leading zeros
            val formattedTime = String.format("%02d:%02d", hour, minute)
            mTime.value = formattedTime
            onTimeSelected(formattedTime)
        }, mHour, mMinute, false
    )

    OutlinedButton(
        onClick = { mTimePickerDialog.show() }
    ) {
        if (mTime.value.isEmpty()) {
            Text("Set Time")
        } else {
            Text(mTime.value)
        }
    }
}

@Composable
fun TimeInput(
    modifier: Modifier = Modifier,
    text: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    // Parse initialTime (HH:mm format)
    val parts = initialTime.split(":")
    val initialHour24 = parts.getOrNull(0)?.toIntOrNull()
    val initialMinute = parts.getOrNull(1)?.toIntOrNull()

    var displayHourText by remember { mutableStateOf("") }
    var minuteText by remember { mutableStateOf("") }
    var isPM by remember { mutableStateOf(false) }

    LaunchedEffect(initialTime) {
        if (initialHour24 != null && initialMinute != null) {
            val currentCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, initialHour24)
                set(Calendar.MINUTE, initialMinute)
            }
            val h12 = currentCal.get(Calendar.HOUR) // 0-11 for Calendar.HOUR, so 12 for 12 AM/PM
            displayHourText = String.format("%02d", if (h12 == 0) 12 else h12) // Display 12 for 0 o'clock (12 AM)
            minuteText = String.format("%02d", initialMinute)
            isPM = currentCal.get(Calendar.AM_PM) == Calendar.PM
        } else {
            // Default to empty or some placeholder if initialTime is invalid
            displayHourText = ""
            minuteText = ""
            isPM = false // Default to AM or based on current time if preferred
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
            // Hour input
            OutlinedTextField(
                value = displayHourText,
                onValueChange = { newValue -> 
                    val num = newValue.take(2).filter { it.isDigit() }
                    if (num.isEmpty()) {
                        displayHourText = ""
                    } else {
                        val intVal = num.toIntOrNull()
                        if (intVal != null && intVal >= 1 && intVal <= 12) {
                            displayHourText = String.format("%02d", intVal) // Keep leading zero if single digit
                        } else if (num.length == 1 && "1".contains(num)) { // Allow typing '1' for 10,11,12
                             displayHourText = num
                        } else if (num.length ==1 && intVal == 0) {
                             displayHourText = num // allow typing '0' for '01' to '09'
                        }
                    }
                    updateTime(displayHourText, minuteText, isPM, onTimeSelected)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Hour") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("HH", textAlign = TextAlign.Center) } // Changed placeholder
            )
            
            Text(":", fontSize = 20.sp)
            
            // Minute input
            OutlinedTextField(
                value = minuteText,
                onValueChange = { newValue -> 
                    val num = newValue.take(2).filter { it.isDigit() }
                     if (num.isEmpty()) {
                        minuteText = ""
                    } else {
                        val intVal = num.toIntOrNull()
                        if (intVal != null && intVal >= 0 && intVal <= 59) {
                            minuteText = String.format("%02d", intVal) // Keep leading zero
                        } else if (num.length == 1 && num.toIntOrNull() != null && num.toInt() <=5 ) {
                           minuteText = num // Allow typing first digit of minutes 0-5
                        } else if (num.length == 1 && num.toIntOrNull() == null) {
                            // allow typing if its not a number yet
                            minuteText = num
                        }
                    }
                    updateTime(displayHourText, minuteText, isPM, onTimeSelected)
                },
                modifier = Modifier.weight(1f),
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("MM", textAlign = TextAlign.Center) } // Changed placeholder
            )
            
            // AM/PM toggle
            Row(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AmPmButton(
                    text = "AM",
                    isSelected = !isPM,
                    onClick = { 
                        isPM = false 
                        updateTime(displayHourText, minuteText, isPM, onTimeSelected)
                    }
                )
                
                AmPmButton(
                    text = "PM",
                    isSelected = isPM,
                    onClick = { 
                        isPM = true
                        updateTime(displayHourText, minuteText, isPM, onTimeSelected)
                    }
                )
            }
        }
    }
}

@Composable
private fun AmPmButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "backgroundColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "textColor"
    )
    
    Box(
        modifier = Modifier
            .width(48.dp)
            .height(56.dp)
            .clip(
                if (text == "AM") 
                    RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                else 
                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
            )
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = if (text == "AM") 
                    RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                else 
                    RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}

private fun updateTime(hourText: String, minuteText: String, isPM: Boolean, onTimeSelected: (String) -> Unit) {
    val hour12 = hourText.toIntOrNull()
    val minute = minuteText.toIntOrNull()

    if (hour12 == null || minute == null) {
        // Or handle partial input, e.g., by calling onTimeSelected with a specific format or null
        // For now, only call back when both are valid numbers.
        // onTimeSelected("") // Indicate invalid/incomplete time to ViewModel if needed
        return
    }
    
    // Convert 12-hour format parts to 24-hour format string
    val hour24 = when {
        isPM && hour12 < 12 -> hour12 + 12
        !isPM && hour12 == 12 -> 0 // 12 AM is 00 hours
        else -> hour12
    }
    
    val formattedTime = String.format("%02d:%02d", hour24, minute)
    onTimeSelected(formattedTime)
}

@Composable
fun TimePickerRow(
    modifier: Modifier = Modifier,
    text: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit
) {
    Row(modifier = modifier) {
        Text(
            text = text,
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        TimePickerComponent(
            initialTime = initialTime,
            onTimeSelected = onTimeSelected
        )
    }
}
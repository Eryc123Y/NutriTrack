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
    val parts = initialTime.split(":")
    val initialHour = if (parts.size >= 1 && parts[0].isNotEmpty()) parts[0].toInt() else 0
    val initialMinute = if (parts.size >= 2 && parts[1].isNotEmpty()) parts[1].toInt() else 0
    
    // Convert to 12-hour format
    val is24Hour = initialHour >= 12
    val hour12 = if (initialHour == 0) 12 else if (initialHour > 12) initialHour - 12 else initialHour
    var hour by remember { mutableStateOf(if (hour12 == 0) "" else hour12.toString()) }
    var minute by remember { mutableStateOf(if (initialMinute == 0) "" else initialMinute.toString()) }
    var isPM by remember { mutableStateOf(is24Hour) }
    
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
                value = hour,
                onValueChange = { newValue -> 
                    // Allow empty input or valid numbers between 1-12
                    if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() in 1..12)) {
                        hour = newValue // Don't pad with zeros for better editing experience
                        updateTime(hour, minute, isPM, onTimeSelected)
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Hour") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("00", textAlign = TextAlign.Center) }
            )
            
            Text(":", fontSize = 20.sp)
            
            // Minute input
            OutlinedTextField(
                value = minute,
                onValueChange = { newValue -> 
                    // Allow empty input or valid numbers between 0-59
                    if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() in 0..59)) {
                        minute = newValue
                        updateTime(hour, minute, isPM, onTimeSelected)
                    }
                },
                modifier = Modifier.weight(1f),
                label = { Text("Min") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
                placeholder = { Text("00", textAlign = TextAlign.Center) }
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
                        updateTime(hour, minute, isPM, onTimeSelected)
                    }
                )
                
                AmPmButton(
                    text = "PM",
                    isSelected = isPM,
                    onClick = { 
                        isPM = true
                        updateTime(hour, minute, isPM, onTimeSelected)
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

private fun updateTime(hour: String, minute: String, isPM: Boolean, onTimeSelected: (String) -> Unit) {
    // If either hour or minute is empty, don't update
    if (hour.isEmpty() || minute.isEmpty()) return
    
    val h = hour.toIntOrNull() ?: return
    val m = minute.toIntOrNull() ?: return
    
    // Convert to 24-hour format
    val hour24 = when {
        isPM && h < 12 -> h + 12
        !isPM && h == 12 -> 0
        else -> h
    }
    
    val formattedTime = String.format("%02d:%02d", hour24, m)
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
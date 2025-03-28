package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserTimePref

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
package com.example.fit2081a1_yang_xingyu_33533563.ui.components

import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fit2081a1_yang_xingyu_33533563.data.model.UserTimePref
import com.example.fit2081a1_yang_xingyu_33533563.util.SharedPreferencesManager

@Composable
fun TimePickerComponent(userID: String,timePrefType: UserTimePref, sharedPref: SharedPreferencesManager) {
    // get the time from SharedPreferences, if not set, use empty string
    val mTime = remember { mutableStateOf(sharedPref.getTimePref(userID, timePrefType)) }
    var mTimePickerDialog = timePicker(userID, sharedPref, mTime, timePrefType)

    OutlinedButton(
        onClick = {
            mTimePickerDialog.show()
        },

    ) {
        if (mTime.value == "") {
            Text("Set Time")
        } else {
            Text(mTime.value)
        }
    }
}

@Composable
fun TimePickerRow(modifier: Modifier = Modifier,
                  text: String = "testString",
                  userID: String = "testUser",
                  timePrefType: UserTimePref = UserTimePref.BIGGEST_MEAL,
                  sharedPref: SharedPreferencesManager = SharedPreferencesManager(LocalContext.current)
                  ) {
    Row(
        modifier = modifier,
    ) {
        Text(
            text = text,
            modifier = Modifier.weight(1f).padding(end = 16.dp),
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        TimePickerComponent(userID, timePrefType, sharedPref)
    }
}

@Composable
fun timePicker(userID: String,
               sharedPref: SharedPreferencesManager,
               mTime: MutableState<String>,
               timePrefType: UserTimePref
               ): TimePickerDialog {
    val mContext = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar.get(Calendar.HOUR_OF_DAY)
    val mMinute = mCalendar.get(Calendar.MINUTE)
    mCalendar.time = Calendar.getInstance().time

    return TimePickerDialog(
        mContext,
        { _, hour: Int, minute: Int ->
            // Format time with leading zeros for better readability
            val formattedTime = String.format("%02d:%02d", hour, minute)
            mTime.value = formattedTime
            // Save to SharedPreferences AFTER the time is selected
            sharedPref.setTimePref(userID, timePrefType, formattedTime)
        }, mHour, mMinute, false
    )
}
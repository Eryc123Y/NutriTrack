package com.example.fit2081a1_yang_xingyu_33533563.data.model

enum class UserTimePref(val timePrefName: String, val questionDescription: String) {
    BIGGEST_MEAL("Breakfast", "What time of day approx, do you have your biggest meal?"),
    SLEEP("Lunch", "What time of day approx, do you go to sleep at night?"),
    WAKEUP("Dinner", "What time of day approx, do you wake up in the morning?"),;
}
package com.example.fit2081a1_yang_xingyu_33533563.data.model

import java.util.Locale

enum class Gender(val genderName: String) {
    MALE("Male"),
    FEMALE("Female");

    companion object {
        fun fromString(gender: String): Gender {
            return when (gender.lowercase(Locale.ROOT)) {
                "Male" -> MALE
                "Female" -> FEMALE
                else -> throw IllegalArgumentException("Invalid gender: $gender")
            }
        }
    }
}
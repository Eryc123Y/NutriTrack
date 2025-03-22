package com.example.fit2081a1_yang_xingyu_33533563.data.model

import java.util.Locale

enum class Gender {
    MALE,
    FEMALE;

    companion object {
        fun fromString(gender: String): Gender {
            return when (gender.lowercase(Locale.ROOT)) {
                "male" -> MALE
                "female" -> FEMALE
                else -> throw IllegalArgumentException("Invalid gender: $gender")
            }
        }
    }
}
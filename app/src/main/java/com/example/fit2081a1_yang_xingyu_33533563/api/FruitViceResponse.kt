package com.example.fit2081a1_yang_xingyu_33533563.api

/**
 * Enum class representing different types of fruit information.
 * Each enum constant has a display name that can be used for UI representation and response
 * object creation.
 */
enum class FruitInfo(val displayName: String) {
    FRUIT_NAME("Fruit Name"),
    FRUIT_FAMILY("Fruit Family"),
    FRUIT_CALORIES("Calories"),
    FRUIT_FAT("Fat"),
    FRUIT_SUGAR("Sugar"),
    FRUIT_CARBOHYDRATE("Carbohydrate"),
    FRUIT_PROTEIN("Protein"),
}

/**
 * Data class representing a response containing fruit information from fruit API.
 *
 * @property fruitInfo A list of maps containing key-value pairs of fruit information. e.g.
 * [{"Fruit Family", "Rosaceae"}, {"Calories", "52"} ... ]
 */
data class FruitResponse(
    var fruitInfo: List<Map<String, String>>
)
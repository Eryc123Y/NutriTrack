package com.example.fit2081a1_yang_xingyu_33533563.data.model

enum class ScoreTypes(
    val maleCsvColumn: String,
    val femaleCsvColumn: String,
    val maxScore: Int,
    val displayName: String
) {
    VEGETABLES("VegetablesHEIFAscoreMale", "VegetablesHEIFAscoreFemale", 10, "Vegetables"),
    FRUITS("FruitHEIFAscoreMale", "FruitHEIFAscoreFemale", 10, "Fruits"),
    GRAINS_CEREALS("GrainsandcerealsHEIFAscoreMale", "GrainsandcerealsHEIFAscoreFemale", 10, "Grains & Cereals"),
    WHOLE_GRAINS("WholegrainsHEIFAscoreMale", "WholegrainsHEIFAscoreFemale", 10, "Whole Grains"),
    MEAT_ALTERNATIVES("MeatandalternativesHEIFAscoreMale", "MeatandalternativesHEIFAscoreFemale", 10, "Meat & Alternatives"),
    DAIRY("DairyandalternativesHEIFAscoreMale", "DairyandalternativesHEIFAscoreFemale", 10, "Dairy"),
    WATER("WaterHEIFAscoreMale", "WaterHEIFAscoreFemale", 5, "Water"),
    UNSATURATED_FATS("UnsaturatedFatHEIFAscoreMale", "UnsaturatedFatHEIFAscoreFemale", 10, "Unsaturated Fats"),
    SODIUM("SodiumHEIFAscoreMale", "SodiumHEIFAscoreFemale", 10, "Sodium"),
    SUGAR("SugarHEIFAscoreMale", "SugarHEIFAscoreFemale", 10, "Sugar"),
    ALCOHOL("AlcoholHEIFAscoreMale", "AlcoholHEIFAscoreFemale", 5, "Alcohol"),
    DISCRETIONARY("DiscretionaryHEIFAscoreMale", "DiscretionaryHEIFAscoreFemale", 10, "Discretionary Foods"),
    TOTAL("HEIFAtotalscoreMale", "HEIFAtotalscoreFemale", 100, "Total Food Quality Score");

    fun getColumnName(gender: Gender): String {
        return when (gender) {
            Gender.MALE -> maleCsvColumn
            Gender.FEMALE -> femaleCsvColumn
        }
    }

    companion object {
        fun fromDisplayName(name: String): ScoreTypes? {
            return ScoreTypes.entries.find { it.displayName.equals(name, ignoreCase = true) }
        }
    }
}

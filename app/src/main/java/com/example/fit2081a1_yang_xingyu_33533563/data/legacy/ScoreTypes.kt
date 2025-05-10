package com.example.fit2081a1_yang_xingyu_33533563.data.legacy

/**
 * Enum class representing the different types of nutrition scores tracked in the app.
 *
 * Each score type has:
 * - A CSV column name for male users
 * - A CSV column name for female users
 * - A maximum possible score
 * - A user-friendly display name
 *
 * This enum is used for mapping between CSV data, database storage, and UI display.
 */
enum class ScoreTypes(
    // The name of the CSV column for male users
    val maleCsvColumn: String,
    // The name of the CSV column for female users
    val femaleCsvColumn: String,
    // The maximum possible score for this type.
    val maxScore: Int,
    // The user-friendly display name for this score type.
    val displayName: String
) {
    TOTAL("HEIFAtotalscoreMale", "HEIFAtotalscoreFemale", 100, "Total Food Quality Score"),
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
    DISCRETIONARY("DiscretionaryHEIFAscoreMale", "DiscretionaryHEIFAscoreFemale", 10, "Discretionary Foods");

    /**
     * Returns the appropriate CSV column name for the given gender.
     *
     * @param gender The gender for which to get the column name.
     * @return The CSV column name corresponding to the gender.
     */
    fun getColumnName(gender: Gender): String {
        return when (gender) {
            Gender.MALE -> maleCsvColumn
            Gender.FEMALE -> femaleCsvColumn
        }
    }

    companion object {
        /**
         * Returns the ScoreType corresponding to the given display name
         *
         * @param name The display name to look up.
         * @return The matching [ScoreTypes], or null if not found.
         */
        fun fromDisplayName(name: String): ScoreTypes? {
            return ScoreTypes.entries.find { it.displayName.equals(name, ignoreCase = true) }
        }

        /**
         * Returns the ScoreTypes corresponding to a CSV column name (male or female).
         *
         * @param columnName The CSV column name to look up.
         * @return The matching ScoreTypes, or null if not found.
         */
        fun fromCsvColumnName(columnName: String): ScoreTypes? {
            return ScoreTypes.entries.find {
                it.maleCsvColumn.equals(columnName, ignoreCase = true) ||
                it.femaleCsvColumn.equals(columnName, ignoreCase = true)
            }
        }
    }
}

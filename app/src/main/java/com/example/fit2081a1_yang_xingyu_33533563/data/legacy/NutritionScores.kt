package com.example.fit2081a1_yang_xingyu_33533563.data.legacy

data class NutritionScores(
    val scores: Map<ScoreTypes, Float> = emptyMap()
) {
    fun getScore(type: ScoreTypes): Float = scores[type] ?: 0f

    fun getTotalScore(): Float = getScore(ScoreTypes.TOTAL)

    companion object {
        fun fromCsvMap(csvRow: Map<String, String>, gender: Gender): NutritionScores {
            val scoreMap = ScoreTypes.entries.associateWith { scoreType ->
                val columnName = scoreType.getColumnName(gender)
                csvRow[columnName]?.toFloatOrNull() ?: 0f
            }

            return NutritionScores(scoreMap)
        }
    }
}

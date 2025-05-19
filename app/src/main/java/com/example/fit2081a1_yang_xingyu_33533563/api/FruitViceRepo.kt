package com.example.fit2081a1_yang_xingyu_33533563.api

import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class FruitViceRepo {
    private val fruitViceService: FruitViceService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.fruityvice.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        fruitViceService = retrofit.create(FruitViceService::class.java)
    }

    /**
     * Fetches fruit details from the FruityVice API and formats the response.
     * We first fetch the raw data using the DTO in the same format as the API response.
     * Then we map the DTO to our desired response format.
     * Includes error handling for API and network issues.
     *
     * @param fruitName The name of the fruit to fetch details for.
     * @return A [FruitResponse] object containing formatted fruit information, or an error message.
     */
    suspend fun getFruitDetails(fruitName: String): FruitResponse {
        return try {
            // Fetch the raw data using the DTO
            val apiDto = fruitViceService.getFruitDetails(fruitName)

            // Transform the DTO into the desired FruitResponse structure
            val formattedInfo = mutableListOf<Map<String, String>>()

            apiDto.name?.let {
                formattedInfo.add(mapOf(FruitInfo.FRUIT_NAME.displayName to it))
            }
            apiDto.family?.let {
                formattedInfo.add(mapOf(FruitInfo.FRUIT_FAMILY.displayName to it))
            }

            // Define a mapping from API nutrition keys to FruitInfo enums
            val nutritionKeyToFruitInfo = mapOf(
                "calories" to FruitInfo.FRUIT_CALORIES,
                "fat" to FruitInfo.FRUIT_FAT,
                "sugar" to FruitInfo.FRUIT_SUGAR,
                "carbohydrates" to FruitInfo.FRUIT_CARBOHYDRATE,
                "protein" to FruitInfo.FRUIT_PROTEIN
            )

            apiDto.nutritions?.let { nutritionMap ->
                nutritionMap.forEach { (key, value) ->
                    nutritionKeyToFruitInfo[key]?.let { fruitInfoEnum ->
                        formattedInfo.add(mapOf(fruitInfoEnum.displayName to value.toString()))
                    }
                }
            }
            // If all data is null or empty, it might also mean the fruit doesn't have much data
            if (formattedInfo.isEmpty() && apiDto.name == null && apiDto.family == null && apiDto.nutritions == null) {
                 FruitResponse(listOf(mapOf("Error" to "Fruit not found or no data available.")))
            } else {
                 FruitResponse(fruitInfo = formattedInfo)
            }
        } catch (e: HttpException) {
            // Handle HTTP errors (e.g., 404 Not Found)
            val errorBody = e.response()?.errorBody()?.string() // Attempt to get more error info
            val errorMessage = if (e.code() == 404) {
                "Fruit '${fruitName}' not found in API."
            } else {
                "API error: ${e.message()} (Code: ${e.code()}). ${errorBody ?: ""}"
            }
            FruitResponse(listOf(mapOf("Error" to errorMessage)))
        } catch (e: IOException) {
            // Handle network errors
            FruitResponse(listOf(mapOf("Error" to "Network error: ${e.message}")))
        } catch (e: Exception) {
            // Handle any other unexpected errors
            FruitResponse(listOf(mapOf("Error" to "An unexpected error occurred: ${e.message}")))
        }
    }
}
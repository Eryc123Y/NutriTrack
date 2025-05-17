package com.example.fit2081a1_yang_xingyu_33533563.api

/**
 * A simple Data Transfer Object (DTO) to directly map the relevant fields
 * from the FruityVice API JSON response.
 * The 'nutritions' JSON object from the API is mapped to a Map.
 */
data class FruityViceApiDto(
    val name: String?, // Fruit's name
    val family: String?, // Fruit's family
    val nutritions: Map<String, Double>?
) 
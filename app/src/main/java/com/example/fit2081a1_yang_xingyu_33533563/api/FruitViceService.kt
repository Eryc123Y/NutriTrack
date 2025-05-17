package com.example.fit2081a1_yang_xingyu_33533563.api

import retrofit2.http.GET
import retrofit2.http.Path

interface FruitViceService {
    @GET("fruit/{name}")
    suspend fun getFruitDetails(@Path("name") fruitName: String): FruityViceApiDto
}
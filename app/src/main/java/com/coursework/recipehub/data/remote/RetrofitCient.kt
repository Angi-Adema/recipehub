
package com.coursework.recipehub.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configures Retrofit to communicate with TheMealDB API.
// Retrofit - Official GitHub Repository
// TheMealDB - API Documentation
object RetrofitClient {

    // The base URL must end with a forward slash.
    private const val BASE_URL =
        "https://www.themealdb.com/api/json/v1/1/"

    // Create the Retrofit instance and configure Gson for JSON conversion.
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Create the MealApiService implementation using Retrofit.
    val mealApiService: MealApiService =
        retrofit.create(MealApiService::class.java)
}

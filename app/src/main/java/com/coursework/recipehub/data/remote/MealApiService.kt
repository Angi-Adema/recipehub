
package com.coursework.recipehub.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

// Define the Retrofit interface for communicating with TheMealDB API.
// Retrofit uses HTTP GET requests to retrieve recipe information.
// Square - Retrofit GitHub Repository
// TheMealDB - API Documentation
interface MealApiService {

    // Search for recipes using the recipe name entered by the user.
    // The @Query annotation adds the recipe name to the URL.
    @GET("search.php")
    suspend fun searchMeals(
        @Query("s") recipeName: String
    ): MealResponseDto

    // Fetch recipes containing the specified main ingredient.
    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): MealResponseDto

    // Fetch the complete details of a recipe using its meal ID.
    @GET("lookup.php")
    suspend fun getMealById(
        @Query("i") id: String
    ): MealResponseDto
}

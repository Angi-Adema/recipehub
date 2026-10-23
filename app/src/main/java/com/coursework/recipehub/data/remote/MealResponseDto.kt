
package com.coursework.recipehub.data.remote

import com.google.gson.annotations.SerializedName

// Represents the JSON response returned by TheMealDB API.
// The "meals" field contains a list of recipes or null when no results are found.
// TheMealDB - API Documentation
data class MealResponseDto(
    @SerializedName("meals")
    val meals: List<MealDto>?
)

// Represents recipe information received from TheMealDB.
// Gson maps the JSON field names to the corresponding Kotlin properties.
// Retrofit - GitHub Documentation
data class MealDto(
    @SerializedName("idMeal")
    val id: String,

    @SerializedName("strMeal")
    val name: String,

    @SerializedName("strMealThumb")
    val imageUrl: String?,

    @SerializedName("strCategory")
    val category: String?,

    @SerializedName("strInstructions")
    val instructions: String?,

    @SerializedName("strIngredient1")
    val ingredient1: String?,

    @SerializedName("strMeasure1")
    val measure1: String?
)




package com.coursework.recipehub.model

// Represents recipe information used throughout the application.
// This model is separate from TheMealDB's JSON response structure.
// Kotlin data classes - Kotlin Documentation
data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val category: String?,
    val instructions: String?,
    val ingredients: List<Ingredient>
)



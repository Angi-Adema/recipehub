package com.coursework.recipehub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Defines the database table used to store favorite recipes.
// Android Developers - Persist data with Room
@Entity(tableName = "favorite_recipes")
data class FavoriteRecipeEntity(

    // TheMealDB recipe ID uniquely identifies each saved recipe.
    @PrimaryKey
    val id: String,

    val name: String,
    val imageUrl: String?,
    val category: String?,
    val instructions: String?,

    // Ingredients are stored as a String for local persistence.
    // The repository will convert between this String and the Ingredient list.
    val ingredients: String
)



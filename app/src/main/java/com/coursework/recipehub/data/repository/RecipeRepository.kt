
package com.coursework.recipehub.data.repository

import com.coursework.recipehub.model.Recipe
import kotlinx.coroutines.flow.Flow

// Defines the operations available for retrieving and saving recipes.
// The repository separates the ViewModel from remote and local data sources.
// Android Developers - Add repository and Manual DI tutorial, Persist data with room, and Kotlin Documentation
interface RecipeRepository {

    // Search for recipes by name using TheMealDB API.
    suspend fun searchRecipes(recipeName: String): Result<List<Recipe>>

    // Search for recipes containing a specified main ingredient.
    suspend fun searchByIngredient(ingredient: String): Result<List<Recipe>>

    // Retrieve complete recipe details using the meal ID.
    suspend fun getRecipeById(id: String): Result<Recipe>

    // Observe recipes saved locally as favorites using Room.
    fun getFavorites(): Flow<List<Recipe>>

    // Save a recipe to the local favorites database.
    suspend fun addFavorite(recipe: Recipe)

    // Remove a recipe from the local favorites database.
    suspend fun removeFavorite(recipe: Recipe)
}

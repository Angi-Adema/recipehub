
package com.coursework.recipehub.data.repository

import com.coursework.recipehub.data.remote.MealDto
import com.coursework.recipehub.data.remote.MealApiService
import com.coursework.recipehub.model.Ingredient
import com.coursework.recipehub.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

// Implements recipe retrieval through TheMealDB API.
// Android Developers - Add repository and Manual DI
// Kotlin Documentation - Result and Coroutines
class RecipeRepositoryImpl(
    private val apiService: MealApiService
) : RecipeRepository {

    // Search for recipes by name. (Kotlin - runCatching and Collection transformations)
    override suspend fun searchRecipes(recipeName: String): Result<List<Recipe>> =
        runCatching {
            apiService.searchMeals(recipeName).meals
                .orEmpty()
                .map { it.toRecipe() }
        }

    // Search for recipes by their main ingredient.
    override suspend fun searchByIngredient(ingredient: String): Result<List<Recipe>> =
        runCatching {
            apiService.filterByIngredient(ingredient).meals
                .orEmpty()
                .map { it.toRecipe() }
        }

    // Retrieve full details for a specific recipe.
    override suspend fun getRecipeById(id: String): Result<Recipe> =
        runCatching {
            val meal = apiService.getMealById(id).meals
                ?.firstOrNull()
                ?: throw NoSuchElementException("Recipe not found")

            meal.toRecipe()
        }

    // Favorites will be implemented when we configure Room.
    override fun getFavorites(): Flow<List<Recipe>> = emptyFlow()

    override suspend fun addFavorite(recipe: Recipe) {
        // Room implementation will be added later.
    }

    override suspend fun removeFavorite(recipe: Recipe) {
        // Room implementation will be added later.
    }

    // Convert TheMealDB response data into the application's Recipe model. (Kotlin - Scope function)
    private fun MealDto.toRecipe(): Recipe {
        val ingredients = listOfNotNull(
            ingredient1?.takeIf { it.isNotBlank() }?.let {
                Ingredient(it, measure1.orEmpty())
            },
            ingredient2?.takeIf { it.isNotBlank() }?.let {
                Ingredient(it, measure2.orEmpty())
            },
            ingredient3?.takeIf { it.isNotBlank() }?.let {
                Ingredient(it, measure3.orEmpty())
            }
        )

        return Recipe(
            id = id,
            name = name,
            imageUrl = imageUrl,
            category = category,
            instructions = instructions,
            ingredients = ingredients
        )
    }
}

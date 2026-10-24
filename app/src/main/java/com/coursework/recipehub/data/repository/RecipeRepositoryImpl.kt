
package com.coursework.recipehub.data.repository

import com.coursework.recipehub.data.remote.MealDto
import com.coursework.recipehub.data.remote.MealApiService
import com.coursework.recipehub.model.Ingredient
import com.coursework.recipehub.model.Recipe
import kotlinx.coroutines.flow.Flow
import com.coursework.recipehub.data.local.FavoriteRecipeDao
import com.coursework.recipehub.data.local.FavoriteRecipeEntity
import kotlinx.coroutines.flow.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Implements recipe retrieval through TheMealDB API.
// Android Developers - Add repository and Manual DI
// Kotlin Documentation - Result and Coroutines
class RecipeRepositoryImpl(
    private val apiService: MealApiService,
    private val favoriteRecipeDao: FavoriteRecipeDao
) : RecipeRepository {

    private val gson = Gson()

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


    // Transform each list retrieved from the Room Flow into the Recipe model.
    // Android Developers - Persist data with Room, Kotlin - Asynchronous Flow
    override fun getFavorites(): Flow<List<Recipe>> =
        favoriteRecipeDao.getAllFavorites().map { favorites ->
            favorites.map { it.toRecipe() }
        }

    // Save a recipe to the local database.
    override suspend fun addFavorite(recipe: Recipe) {
        favoriteRecipeDao.insertFavorite(recipe.toEntity())
    }

    // Remove a recipe from the local database.
    override suspend fun removeFavorite(recipe: Recipe) {
        favoriteRecipeDao.deleteFavorite(recipe.toEntity())
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

    // Convert a Recipe into a Room entity for local storage.
    // Google - Gson user guide
    private fun Recipe.toEntity(): FavoriteRecipeEntity {
        return FavoriteRecipeEntity(
            id = id,
            name = name,
            imageUrl = imageUrl,
            category = category,
            instructions = instructions,
            ingredients = gson.toJson(ingredients)
        )
    }

    // Convert a Room entity back into the application's Recipe model.
    // Google Gson User Guide - Deserialization and TypeToken
    private fun FavoriteRecipeEntity.toRecipe(): Recipe {
        val ingredientType = object :
            TypeToken<List<Ingredient>>() {}.type

        val ingredientList: List<Ingredient> =
            gson.fromJson(ingredients, ingredientType)

        return Recipe(
            id = id,
            name = name,
            imageUrl = imageUrl,
            category = category,
            instructions = instructions,
            ingredients = ingredientList
        )
    }

}

package com.coursework.recipehub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coursework.recipehub.data.repository.RecipeRepository
import com.coursework.recipehub.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

// Manages recipe search data and UI state.
// Android Developers - ViewModel overview
// Kotlin Documentation - StateFlow and SharedFlow
class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    // Holds the current list of recipes displayed by the UI.
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

    // Store the recipe selected by the user for viewing full details.
    // Kotlin Documentation - StateFlow and SharedFlow
    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)

    // Observe recipes saved in the local Room database.
    // Android Developers - StateFlow and Room
    val favorites: StateFlow<List<Recipe>> = repository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val selectedRecipe: StateFlow<Recipe?> = _selectedRecipe.asStateFlow()

    // Indicates whether a recipe search is in progress.
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Holds an error message if a search fails.
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Search for recipes by name using the repository.
    fun searchRecipes(recipeName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.searchRecipes(recipeName)
                    .onSuccess { result ->
                        _recipes.value = result
                    }
                    .onFailure { error ->
                        // Display a user-friendly message when a network request fails.
                        // Kotlin Documentation - Exception handling
                        _errorMessage.value =
                            if (error is java.io.IOException) {
                                "Unable to connect. Please check your internet connection and try again."
                            } else {
                                error.message ?: "Unable to retrieve recipes."
                            }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Search for recipes by ingredient using the repository.
    // Android Developers - ViewModel overview
    // Kotlin Documentation - Coroutines and Result
    fun searchByIngredient(ingredient: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                repository.searchByIngredient(ingredient)
                    .onSuccess { result ->
                        _recipes.value = result
                    }
                    .onFailure { error ->
                        // Clear previous search results and display a user-friendly error.
                        // Kotlin Documentation - Exception handling
                        _recipes.value = emptyList()

                        _errorMessage.value =
                            if (error is java.io.IOException) {
                                "Unable to connect. Please check your internet connection and try again."
                            } else {
                                error.message ?: "Unable to retrieve recipes."
                            }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Retrieve complete recipe details from TheMealDB using the recipe ID.
    // Android Developers - ViewModel overview
    // Kotlin Documentation - Coroutines and Result
    fun loadRecipeDetails(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _selectedRecipe.value = null

            try {
                repository.getRecipeById(id)
                    .onSuccess { recipe ->
                        _selectedRecipe.value = recipe
                    }
                    .onFailure { error ->
                        // Display a user-friendly message when a network request fails.
                        // Kotlin Documentation - Exception handling
                        _errorMessage.value =
                            if (error is java.io.IOException) {
                                "Unable to connect. Please check your internet connection and try again."
                            } else {
                                error.message ?: "Unable to retrieve recipe details."
                            }
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear the selected recipe to return to the home screen.
    // Android Developers - State and Jetpack Compose
    fun clearSelectedRecipe() {
        _selectedRecipe.value = null
    }

    // Display a locally saved recipe without making an API request.
    // Android Developers - ViewModel overview, State and Jetpack Compose
    fun selectSavedRecipe(recipe: Recipe) {
        _selectedRecipe.value = recipe
    }

    // Save the selected recipe to the local Room database.
    // Android Developers - ViewModel overview, Coroutines, Save data in a local database using Room
    fun saveFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.addFavorite(recipe)
            } catch (error: Exception) {
                _errorMessage.value =
                    error.message ?: "Unable to save recipe to favorites."
            }
        }
    }

    // Remove a saved recipe from the local Room database.
    // Android Developers - ViewModel overview, Coroutines, Room
    fun removeFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                repository.removeFavorite(recipe)
            } catch (error: Exception) {
                _errorMessage.value =
                    error.message ?: "Unable to remove recipe from favorites."
            }
        }
    }
}

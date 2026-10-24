package com.coursework.recipehub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coursework.recipehub.data.repository.RecipeRepository
import com.coursework.recipehub.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Manages recipe search data and UI state.
// Android Developers - ViewModel overview
// Kotlin Documentation - StateFlow and SharedFlow
class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    // Holds the current list of recipes displayed by the UI.
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes.asStateFlow()

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
                        _errorMessage.value =
                            error.message ?: "Unable to retrieve recipes."
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
                        _recipes.value = emptyList()
                        _errorMessage.value =
                            error.message ?: "Unable to retrieve recipes."
                    }
            } finally {
                _isLoading.value = false
            }
        }
    }

}

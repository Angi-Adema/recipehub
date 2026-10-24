package com.coursework.recipehub


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.coursework.recipehub.data.local.RecipeDatabaseProvider
import com.coursework.recipehub.data.remote.RetrofitClient
import com.coursework.recipehub.data.repository.RecipeRepositoryImpl
import com.coursework.recipehub.ui.viewmodel.RecipeViewModel
import com.coursework.recipehub.ui.viewmodel.RecipeViewModelFactory
import androidx.compose.foundation.clickable
import com.coursework.recipehub.ui.screens.RecipeDetailsScreen
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription


// The MainActivity file is the entry point for the Recipe Hub application.
// Jetpack Compose is used to create the user interface and Material 3 is used for styling.
// Android Developers - Jetpack Compose Tutorial
class MainActivity : ComponentActivity() {

    // This function is called when the application is opened
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtain the Room database and DAO for locally saved recipes.
        // Android Developers - Persist data with Room
        val database = RecipeDatabaseProvider.getDatabase(applicationContext)
        val favoriteRecipeDao = database.favoriteRecipeDao()

        // Connect the remote API and local database to the repository.
        // Android Developers - Add repository and Manual DI
        val repository = RecipeRepositoryImpl(
            apiService = RetrofitClient.mealApiService,
            favoriteRecipeDao = favoriteRecipeDao
        )

        // Create the ViewModel with its repository dependency.
        // Android Developers - Create ViewModels with dependencies
        val factory = RecipeViewModelFactory(repository)
        val recipeViewModel = ViewModelProvider(this, factory)[RecipeViewModel::class.java]

        setContent {
            MaterialTheme {

                // Observe the selected recipe to determine which screen to display.
                // Android Developers - State and Jetpack Compose
                val selectedRecipe by recipeViewModel.selectedRecipe.collectAsState()

                if (selectedRecipe != null) {

                    // Fade in the recipe details screen when a recipe is selected.
                    // Android Developers - Animation in Compose
                    var isVisible by remember(selectedRecipe?.id) {
                        mutableStateOf(false)
                    }

                    LaunchedEffect(selectedRecipe?.id) {
                        isVisible = true
                    }

                    val alpha by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = tween(durationMillis = 300),
                        label = "Recipe details fade-in"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { this.alpha = alpha }
                    ) {

                        // Return to the home screen without clearing search results.
                        // Android Developers - Button
                        Button(
                            onClick = {
                                recipeViewModel.clearSelectedRecipe()
                            },
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text("Back to Recipes")
                        }

                        // Display the selected recipe's complete details.
                        selectedRecipe?.let { recipe ->

                            // Display recipe details and handle requests to save favorites.
                            // Android Developers - State and Jetpack Compose, Button
                            RecipeDetailsScreen(
                                recipe = recipe,
                                onSaveFavorite = {
                                    recipeViewModel.saveFavorite(recipe)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                } else {

                    // Display the recipe search and browsing screen.
                    RecipeHubHomeScreen(recipeViewModel = recipeViewModel)
                }
            }
        }

    }
}

// Create the RecipeHub homescreen where users can enter a recipe name, search by an ingredient, or
// browse various ingredient categories. @Composable builds the UI
@Composable
fun RecipeHubHomeScreen(recipeViewModel: RecipeViewModel) {

    // Store the text entered into the recipe name and ingredient fields
    // Use remember to store these values in recomposition
    // mutableStateOf enables Compose to see the changes and update the UI
    // Android Developers - State and Jetpack Compose
    var recipeName by remember { mutableStateOf("") }
    var ingredient by remember { mutableStateOf("") }

    // Observe recipe results, loading status, and errors from the ViewModel.
    // Android Developers - State and Jetpack Compose
    val recipes by recipeViewModel.recipes.collectAsState()
    val isLoading by recipeViewModel.isLoading.collectAsState()
    val errorMessage by recipeViewModel.errorMessage.collectAsState()

    // Observe recipes saved in the local Room database.
    // Android Developers - State and Jetpack Compose
    val favorites by recipeViewModel.favorites.collectAsState()

    // Track whether the user is viewing saved favorites.
    var showFavorites by remember { mutableStateOf(false) }

    // Observe the selected recipe to display its detailed information.
    // Android Developers - State and Jetpack Compose
    val selectedRecipe by recipeViewModel.selectedRecipe.collectAsState()

    // Create the main ingredient categories to browse by
    val categories = listOf(
        "Chicken", "Beef", "Pork", "Fish", "Vegetable"
    )

    // Scaffold provides Material Design 3 screen structure and top app bar
    // Android Developers - Scaffold
    Scaffold(
        topBar = {
            // Opt in to the experimental Material 3 API required by the installed library version
            // Android Developers - Migrate from Material 2 to Material 3 in Compose, CenterAlignedTopAppBar
            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun titleBar() {

                // Center the title at the top of the screen
                CenterAlignedTopAppBar(
                    title = { Text("Recipe Hub") }
                )
            }
            titleBar()   // Display the top app bar in the scaffold
        }
    ) { paddingValues ->

        // Arrange home screen components vertically using Column
        // Use padding to prevent the content from overlapping the app bar and include spacing around the content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Create and display the intro heading in the home screen
            // Identify the home screen title as a heading for screen readers.
            // Android Developers - Semantics in Compose
            Text(
                text = "Find your next meal!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics {
                    heading()
                }
            )

            // Switch between the saved favorites list and recipe searches.
            // Android Developers - Button, State and Jetpack Compose
            OutlinedButton(
                onClick = { showFavorites = !showFavorites },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (showFavorites) "Back to Recipe Search"
                    else "View Favorites (${favorites.size})"
                )
            }

            // Text field for entering a recipe name, onValueChange updates recipe name as it is typed in
            // Android Developers - Configure text fields
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Search by recipe name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button to search recipes by name
            // Kotlin - isNotBlank()
            Button(
                onClick = {
                    if (recipeName.isNotBlank()) {
                        recipeViewModel.searchRecipes(recipeName.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Recipes")
            }

            // Text field for entering an ingredient
            OutlinedTextField(
                value = ingredient,
                onValueChange = { ingredient = it },
                label = { Text("Search by ingredient") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button to search recipes by ingredient
            // Android Developers - Button, Kotlin Documentation - String functions
            Button(
                onClick = {
                    if (ingredient.isNotBlank()) {
                        recipeViewModel.searchByIngredient(ingredient.trim())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Ingredients")
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (showFavorites) {

                    // Display recipes saved in the local Room database.
                    // Android Developers - Lists and grids, State and Jetpack Compose
                    item {
                        // Identify the saved favorites section as a heading for screen readers.
                        // Android Developers - Semantics in Compose
                        Text(
                            text = "Saved Favorites",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.semantics {
                                heading()
                            }
                        )
                    }

                    if (favorites.isEmpty()) {
                        item {
                            Text("No favorite recipes saved yet.")
                        }
                    }

                    items(favorites) { recipe ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClickLabel = "View saved recipe ${recipe.name}"
                                ) {
                                    // Open the locally saved recipe without requesting data from TheMealDB.
                                    // Android Developers - State and Jetpack Compose
                                    // Android Developers - Semantics in Compose
                                    recipeViewModel.selectSavedRecipe(recipe)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium
                            )


                            recipe.category?.let { category ->
                                Text(text = category)
                            }

                            // Remove this saved recipe from the local Room database.
                            // Android Developers - Button, Room
                            OutlinedButton(
                                onClick = {
                                    recipeViewModel.removeFavorite(recipe)
                                }
                            ) {
                                Text("Remove from Favorites")
                            }

                            HorizontalDivider()
                        }
                    }

                } else {

                    // Display the existing ingredient categories.
                    item {
                        Text("Browse by Main Ingredient")
                    }

                    items(categories) { category ->
                        OutlinedButton(
                            onClick = {
                                ingredient = category
                                recipeViewModel.searchByIngredient(category)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(category)
                        }
                    }

                    // Display loading indicators and search errors.
                    item {

                        // Display a loading indicator with a descriptive accessibility label.
                        // Android Developers - Progress indicators, Semantics in Compose
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.semantics {
                                    contentDescription = "Loading recipes"
                                }
                            )
                        }


                        errorMessage?.let { error ->
                            Text(text = error)
                        }

                        if (!isLoading && errorMessage == null && recipes.isEmpty()) {
                            Text("Search for a recipe to see results.")
                        }
                    }

                    // Display recipes returned by the search.
                    items(recipes) { recipe ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    onClickLabel = "View details for ${recipe.name}"
                                ) {
                                    recipeViewModel.loadRecipeDetails(recipe.id)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = recipe.name,
                                style = MaterialTheme.typography.titleMedium
                            )


                            recipe.category?.let { category ->
                                Text(text = category)
                            }

                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

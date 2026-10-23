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

// The MainActivity file is the entry point for the Recipe Hub application.
// Jetpack Compose is used to create the user interface and Material 3 is used for styling.
// Android Developers - Jetpack Compose Tutorial
class MainActivity : ComponentActivity() {

    // This function is called when the application is opened
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent sets up the Compose UI, Material 3 styling is applied, and homescreen is displayed
        setContent {
            MaterialTheme {
                RecipeHubHomeScreen()
            }
        }
    }
}

// Create the RecipeHub homescreen where users can enter a recipe name, search by an ingredient, or
// browse various ingredient categories. @Composable builds the UI
@Composable
fun RecipeHubHomeScreen() {

    // Store the text entered into the recipe name and ingredient fields
    // Use remember to store these values in recomposition
    // mutableStateOf enables Compose to see the changes and update the UI
    // Android Developers - State and Jetpack Compose
    var recipeName by remember { mutableStateOf("") }
    var ingredient by remember { mutableStateOf("") }

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
            Text(
                text = "Find your next meal!",
                style = MaterialTheme.typography.headlineSmall
            )

            // Text field for entering a recipe name, onValueChange updates recipe name as it is typed in
            // Android Developers - Configure text fields
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Search by recipe name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Button to search recipes by name
            Button(
                onClick = { /* Search functionality later */ },
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
            Button(
                onClick = { /* Ingredient search later */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Ingredients")
            }

            // Display the ingredient search heading
            Text(
                text = "Browse by Main Ingredient",
                style = MaterialTheme.typography.titleMedium
            )

            // Use LazyColumn to display the vertical scrolling list of ingredient categories
            // Android Developers - Lazy lists and lazy grids
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Create outlined buttons for each category
                // Selecting a category will start the search
                // Android Developers - OutlinedButton
                items(categories) { category ->
                    OutlinedButton(
                        onClick = { /* Category search later */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(category)
                    }
                }
            }
        }
    }
}

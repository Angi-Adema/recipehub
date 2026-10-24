
package com.coursework.recipehub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.coursework.recipehub.model.Recipe

// Displays the selected recipe's details, ingredients, and instructions.
// Android Developers - Lists and grids, Jetpack Compose, Material Design 3 in Compose, Text in Jetpack Compose
@Composable
fun RecipeDetailsScreen(
    recipe: Recipe,
    onSaveFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Display recipe details in a vertically scrollable list.
    // Android Developers - Lists and grids
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Display the selected recipe's name and category.
        item {
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.headlineMedium
            )

            recipe.category?.let { category ->
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // Allow the user to save the selected recipe to the Room database.
        // Android Developers - Button
        item {
            Button(
                onClick = onSaveFavorite,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save to Favorites")
            }
        }

        // Display the complete list of ingredients and measurements.
        // Android Developers - Lists and grids
        item {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(recipe.ingredients) { ingredient ->
            Text(
                text = "${ingredient.measurement} ${ingredient.name}".trim()
            )
        }

        // Display the recipe's cooking instructions.
        item {
            HorizontalDivider()

            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = recipe.instructions
                    ?: "No instructions available."
            )
        }
    }
}



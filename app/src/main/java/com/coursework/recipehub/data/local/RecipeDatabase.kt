package com.coursework.recipehub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Defines the Room database for storing favorite recipes.
// Android Developers - Persist data with Room
@Database(
    entities = [FavoriteRecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {

    // Provides access to the database operations for favorite recipes.
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}

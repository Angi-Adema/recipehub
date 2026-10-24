package com.coursework.recipehub.data.local

import android.content.Context
import androidx.room.Room

// Creates and provides a single Room database instance.
// Android Developers - Persist data with Room
object RecipeDatabaseProvider {

    @Volatile
    private var INSTANCE: RecipeDatabase? = null

    // Return the existing database or create it when first needed.
    fun getDatabase(context: Context): RecipeDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                RecipeDatabase::class.java,
                "recipe_database"
            ).build().also {
                INSTANCE = it
            }

            instance
        }
    }
}

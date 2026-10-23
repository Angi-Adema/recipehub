package com.coursework.recipehub.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Defines database operations for locally stored favorite recipes.
// Android Developers - Persist data with Room
@Dao
interface FavoriteRecipeDao {

    // Observe saved favorites and receive updates when the table changes.
    @Query("SELECT * FROM favorite_recipes ORDER BY name ASC")
    fun getAllFavorites(): Flow<List<FavoriteRecipeEntity>>

    // Save a favorite, replacing an existing record with the same recipe ID.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(recipe: FavoriteRecipeEntity)

    // Remove a saved favorite from the database.
    @Delete
    suspend fun deleteFavorite(recipe: FavoriteRecipeEntity)
}

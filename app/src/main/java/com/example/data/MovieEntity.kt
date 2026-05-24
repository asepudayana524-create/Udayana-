package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: String,
    val title: String,
    val director: String,
    val releaseYear: Int,
    val genre: String,
    val rating: Double,
    val synopsis: String,
    val isFavorite: Boolean = false,
    val isOnWatchlist: Boolean = false,
    val userNotes: String = "",
    val posterColorStart: String = "#FF2C3A", // Hex color
    val posterColorEnd: String = "#FF8A00"   // Hex color
) : Serializable

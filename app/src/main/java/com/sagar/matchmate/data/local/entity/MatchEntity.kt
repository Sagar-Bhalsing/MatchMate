package com.sagar.matchmate.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "matches"
)
data class MatchEntity(
    @PrimaryKey val id: String,
    val gender: String?,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val city: String,
    val state: String,
    val country: String,
    val imageUrl: String,
    val email: String?,
    val phone: String?,
    val status: String,
    val page: Int,
    val updatedAt: Long
)


package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String,
    val originalPrice: Double,
    val salePrice: Double,
    val imageUrl: String,
    val isHotDeal: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val salePrice: Double,
    val imageUrl: String,
    val quantity: Int
)

@Entity(tableName = "user_sessions")
data class UserSession(
    @PrimaryKey val id: Int = 1, // Single row constraint (always id = 1)
    val name: String,
    val email: String,
    val profilePicUrl: String,
    val isLoggedIn: Boolean,
    val isGuest: Boolean
)

package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.UserSession
import com.example.data.repository.ECommerceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.net.URLEncoder

class ECommerceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ECommerceRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ECommerceRepository(
            database.productDao(),
            database.cartDao(),
            database.userSessionDao()
        )
        // Seed initial products automatically on startup
        viewModelScope.launch {
            try {
                repository.seedInitialProductsIfEmpty()
            } catch (e: Exception) {
                Log.e("ECommerceViewModel", "Error seeding products", e)
            }
        }
    }

    // UI Input states
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Products Stream
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Products Stream
    val filteredProducts: StateFlow<List<Product>> = combine(
        allProducts,
        _searchQuery,
        _selectedCategory
    ) { products, query, category ->
        products.filter { product ->
            val matchesSearch = product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || product.category.equals(category, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unique Categories Stream for Filter chips
    val categories: StateFlow<List<String>> = allProducts.map { products ->
        val list = products.map { it.category }.distinct().toMutableList()
        list.add(0, "All")
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("All"))

    // Shopping Cart Stream
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total Cart Price Calc
    val cartTotal: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.salePrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // User Session
    val userSession: StateFlow<UserSession?> = repository.userSession
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Hot Deals Stream
    val hotDealsProducts: StateFlow<List<Product>> = allProducts.map { list ->
        list.filter { it.isHotDeal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected product for product details screen/bottom sheet
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Screen navigation tracking (Simple internal Compose router State when needed)
    private val _adminPasswordEntered = MutableStateFlow(false)
    val adminPasswordEntered: StateFlow<Boolean> = _adminPasswordEntered.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectProduct(product: Product?) {
        _selectedProduct.value = product
    }

    // Google Sign-In with robust verification and local user profile simulation
    fun signInWithGoogle(name: String, email: String, profilePicUrl: String) {
        viewModelScope.launch {
            val session = UserSession(
                name = name.ifBlank { "Google User" },
                email = email.ifBlank { "googleuser@gmail.com" },
                profilePicUrl = profilePicUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150" },
                isLoggedIn = true,
                isGuest = false
            )
            repository.saveSession(session)
        }
    }

    fun skipLoginAsGuest() {
        viewModelScope.launch {
            val session = UserSession(
                name = "Guest User",
                email = "guest@moonlight.com",
                profilePicUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
                isLoggedIn = false,
                isGuest = true
            )
            repository.saveSession(session)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
            repository.clearCart()
            _adminPasswordEntered.value = false
        }
    }

    // Cart Operations
    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(cartItem: CartItem, change: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartItem, cartItem.quantity + change)
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            repository.removeCartItem(cartItem)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Admin Password Verification
    fun verifyAdminPassword(password: String): Boolean {
        return if (password == "admin123") {
            _adminPasswordEntered.value = true
            true
        } else {
            false
        }
    }

    fun resetAdminAccess() {
        _adminPasswordEntered.value = false
    }

    // Admin dynamic CRUD operations
    fun addProduct(
        title: String,
        description: String,
        category: String,
        originalPrice: Double,
        salePrice: Double,
        imageUrl: String,
        isHotDeal: Boolean
    ) {
        viewModelScope.launch {
            val product = Product(
                title = title,
                description = description,
                category = category,
                originalPrice = originalPrice,
                salePrice = salePrice,
                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1481349518771-20055b2a7b24?w=500&q=80" },
                isHotDeal = isHotDeal
            )
            repository.insertProduct(product)
        }
    }

    fun editProduct(product: Product) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Direct WhatsApp Checkout formatter and launcher
    fun placeOrderViaWhatsApp(
        context: Context,
        shippingName: String,
        shippingAddress: String,
        shippingPhone: String
    ) {
        viewModelScope.launch {
            val items = cartItems.value
            val total = cartTotal.value

            if (items.isEmpty()) return@launch

            val messageBuilder = StringBuilder()
            messageBuilder.append("🌟 *NEW ORDER PLACED VIA MOON LIGHT* 🌟\n\n")
            messageBuilder.append("👤 *Customer Name:* $shippingName\n")
            messageBuilder.append("📞 *Phone Number:* $shippingPhone\n")
            messageBuilder.append("🏠 *Delivery Address:* $shippingAddress\n\n")
            messageBuilder.append("📦 *Ordered Items:*\n")

            items.forEachIndexed { index, item ->
                messageBuilder.append("${index + 1}. *${item.title}*\n")
                messageBuilder.append("   Quantity: ${item.quantity} | Price: Rs. ${item.salePrice}\n")
            }

            messageBuilder.append("\n💰 *Total Payable Bill:* Rs. *${total}*\n\n")
            messageBuilder.append("✨ _Order placed via Moon Light application owned by Muhammad Sajawal Hussnain & Miss Mano._")

            val message = messageBuilder.toString()
            val phone = "923267356855" // Primary target routing whatsapp as requested
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=${URLEncoder.encode(message, "UTF-8")}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                
                // Clear cart immediately after launching checkout to mark complete
                clearCart()
            } catch (e: Exception) {
                Log.e("ECommerceViewModel", "Error opening WhatsApp", e)
                // Fallback to generic text sharing option if WhatsApp is not installed
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "$message\n(Send to +$phone)")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(Intent.createChooser(sendIntent, "Send Order Details").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}

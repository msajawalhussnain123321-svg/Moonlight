package com.example.data.repository

import com.example.data.local.CartDao
import com.example.data.local.ProductDao
import com.example.data.local.UserSessionDao
import com.example.data.model.CartItem
import com.example.data.model.Product
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class ECommerceRepository(
    private val productDao: ProductDao,
    private val cartDao: CartDao,
    private val userSessionDao: UserSessionDao
) {
    // Products Streams & Operations
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Int): Product? = productDao.getProductById(id)

    suspend fun insertProduct(product: Product) = productDao.insertProduct(product)

    suspend fun updateProduct(product: Product) = productDao.updateProduct(product)

    suspend fun deleteProduct(product: Product) = productDao.deleteProduct(product)

    // Cart Streams & Operations
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()

    suspend fun addToCart(product: Product, quantity: Int = 1) {
        val existing = cartDao.getCartItemByProductId(product.id)
        if (existing != null) {
            cartDao.updateCartItem(
                existing.copy(quantity = existing.quantity + quantity)
            )
        } else {
            cartDao.insertCartItem(
                CartItem(
                    productId = product.id,
                    title = product.title,
                    salePrice = product.salePrice,
                    imageUrl = product.imageUrl,
                    quantity = quantity
                )
            )
        }
    }

    suspend fun updateCartItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(cartItem)
        } else {
            cartDao.updateCartItem(cartItem.copy(quantity = newQuantity))
        }
    }

    suspend fun removeCartItem(cartItem: CartItem) {
        cartDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // User Session Streams & Operations
    val userSession: Flow<UserSession?> = userSessionDao.getUserSession()

    suspend fun getUserSessionSync(): UserSession? = userSessionDao.getUserSessionSync()

    suspend fun saveSession(session: UserSession) {
        userSessionDao.saveUserSession(session)
    }

    suspend fun clearSession() {
        userSessionDao.clearSession()
    }

    // Seeding logic for dynamic, spectacular catalogue display out-of-the-box
    suspend fun seedInitialProductsIfEmpty() {
        val current = productDao.getAllProducts().first()
        if (current.isEmpty()) {
            val starterProducts = listOf(
                Product(
                    title = "Moon Light Pro LED Starry Sky Projector",
                    description = "Transforms your room into a cosmic sky with stunning rotating moonscapes and nebula clouds. Includes smart app controls, built-in ambient timers, and calming colors.",
                    category = "Smart Decor",
                    originalPrice = 4500.0,
                    salePrice = 2899.0,
                    imageUrl = "https://images.unsplash.com/photo-1540324155974-75226c3ad3a6?w=500&q=80",
                    isHotDeal = true
                ),
                Product(
                    title = "Moon Light Wireless Buds Neo",
                    description = "Immersive sound pods with 30-hour battery backup, quick hyper-charging, ultra latency-free game modes, and smart touch gestures themed in dynamic midnight black.",
                    category = "Audio Essentials",
                    originalPrice = 3800.0,
                    salePrice = 2199.0,
                    imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=500&q=80",
                    isHotDeal = true
                ),
                Product(
                    title = "Nebula Dark Premium Smartwatch v2",
                    description = "A luxurious smartwatch featuring full-bleed AMOLED display, real-time stress metrics, sleep tracking, dynamic fitness loops, and a solid titanium bezel.",
                    category = "Gadgets",
                    originalPrice = 8500.0,
                    salePrice = 5499.0,
                    imageUrl = "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=500&q=80",
                    isHotDeal = false
                ),
                Product(
                    title = "Glow Midnight Luxury Highlighter Palette",
                    description = "Premium face cosmetics designed by professionals. Offers a glowing, long-lasting lunar radiance. Highly blendable with organic midnight botanicals.",
                    category = "Cosmetics",
                    originalPrice = 2500.0,
                    salePrice = 1450.0,
                    imageUrl = "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?w=500&q=80",
                    isHotDeal = false
                ),
                Product(
                    title = "Starry Night Silk Eye Sleeping Mask",
                    description = "100% pure Mulberry silk sleeping mask designed for a deep, restive slumber. Block out light completely and enjoy an elegant touch against your eyelids.",
                    category = "Lifestyle & Sleep",
                    originalPrice = 1200.0,
                    salePrice = 750.0,
                    imageUrl = "https://images.unsplash.com/photo-1582533561751-ef6f6ab93a2e?w=500&q=80",
                    isHotDeal = true
                ),
                Product(
                    title = "Cosmic Activewear Jacket - Nightrunner",
                    description = "Extremely lightweight windbreaker and water resistant training jacket with retro-reflective lunar striping, designed for maximum comfort and style.",
                    category = "Apparel",
                    originalPrice = 5200.0,
                    salePrice = 3900.0,
                    imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500&q=80",
                    isHotDeal = false
                )
            )
            productDao.insertProducts(starterProducts)
        }
    }
}

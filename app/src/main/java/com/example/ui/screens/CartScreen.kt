package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CartItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.ECommerceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: ECommerceViewModel,
    onNavigateBack: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val userSession by viewModel.userSession.collectAsState()

    var shippingName by remember { mutableStateOf("") }
    var shippingAddress by remember { mutableStateOf("") }
    var shippingPhone by remember { mutableStateOf("") }

    var formError by remember { mutableStateOf(false) }
    var showSuccessFeedback by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Automatically fill profile name if logged in
    LaunchedEffect(userSession) {
        userSession?.let {
            if (!it.isGuest && it.name.isNotBlank()) {
                shippingName = it.name
                if (it.email.isNotBlank() && it.email != "googleuser@gmail.com") {
                    // pre-load email as phone starter or keep focus clean
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = LunarGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("My Shopping Cart", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (cartItems.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Clear all", tint = SlashedPriceColor)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LunarSurface)
            )
        },
        containerColor = MidnightBlue
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
        ) {
            if (cartItems.isEmpty()) {
                // Empty Shopping Cart Visual Vibe
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RemoveShoppingCart,
                        contentDescription = "No items",
                        tint = NeutralTextMuted,
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Cart is Empty",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Browse Moon Light catalog and add hot items with slashed prices!",
                        color = NeutralTextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = LunarGold, contentColor = NeutralDark)
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Continue Shopping")
                    }
                }
            } else {
                // Cart Items + Check out panel
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Items List (Scrollable top portion)
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(cartItems) { item ->
                            CartProductItem(
                                item = item,
                                onQuantityDecrease = { viewModel.updateCartQuantity(item, -1) },
                                onQuantityIncrease = { viewModel.updateCartQuantity(item, 1) },
                                onRemove = { viewModel.removeFromCart(item) }
                            )
                        }

                        // Form input module
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = LunarSurface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = LunarGold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Checkout Delivery Form",
                                            color = Color.White,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = shippingName,
                                        onValueChange = {
                                            shippingName = it
                                            formError = false
                                        },
                                        label = { Text("Receiver Full Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = LunarGold,
                                            focusedLabelColor = LunarGold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = shippingAddress,
                                        onValueChange = {
                                            shippingAddress = it
                                            formError = false
                                        },
                                        label = { Text("Complete Delivery Address") },
                                        minLines = 2,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = LunarGold,
                                            focusedLabelColor = LunarGold
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = shippingPhone,
                                        onValueChange = {
                                            shippingPhone = it
                                            formError = false
                                        },
                                        label = { Text("Contact Phone Number") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = LunarGold,
                                            focusedLabelColor = LunarGold
                                        )
                                    )

                                    if (formError) {
                                        Text(
                                            text = "Please complete all fields to place order.",
                                            color = SlashedPriceColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(top = 10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Fixed Bottom Checkout Summary Drawer
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = LunarSurface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Summary Price", color = NeutralTextMuted, fontSize = 13.sp)
                                Text(
                                    "Rs. $cartTotal",
                                    color = LunarGold,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Divider(color = LunarSurfaceLight, modifier = Modifier.padding(bottom = 14.dp))

                            // checkout route
                            Button(
                                onClick = {
                                    if (shippingName.isBlank() || shippingAddress.isBlank() || shippingPhone.isBlank()) {
                                        formError = true
                                    } else {
                                        viewModel.placeOrderViaWhatsApp(
                                            context = context,
                                            shippingName = shippingName,
                                            shippingAddress = shippingAddress,
                                            shippingPhone = shippingPhone
                                        )
                                        showSuccessFeedback = true
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("submit_button"),
                                shape = RoundedCornerShape(25.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LunarGold, contentColor = NeutralDark)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Buy Via WhatsApp", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// STYLED SINGLE CART ITEM REMOVE CARD
@Composable
fun CartProductItem(
    item: CartItem,
    onQuantityDecrease: () -> Unit,
    onQuantityIncrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LunarSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text description details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Price: Rs. ${item.salePrice.toInt()}",
                        color = LunarGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quantity selector layout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onQuantityDecrease,
                        modifier = Modifier
                            .background(LunarSurfaceLight, CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(12.dp))
                    }

                    Text(
                        text = item.quantity.toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onQuantityIncrease,
                        modifier = Modifier
                            .background(LunarSurfaceLight, CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
            }

            // Quick Remove Bin icon
            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove product",
                    tint = SlashedPriceColor
                )
            }
        }
    }
}

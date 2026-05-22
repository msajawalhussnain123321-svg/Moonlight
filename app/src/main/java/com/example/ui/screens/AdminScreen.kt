package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.theme.*
import com.example.ui.viewmodel.ECommerceViewModel
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: ECommerceViewModel,
    onNavigateBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf(0) } // 0 = Catalog list, 1 = Add Form

    // Form states for creating a product
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Smart Decor") }
    var description by remember { mutableStateOf("") }
    var originalPriceStr by remember { mutableStateOf("") }
    var salePriceStr by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isHotDeal by remember { mutableStateOf(false) }

    var formMessage by remember { mutableStateOf("") }
    var formSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(context, it)
            if (savedPath != null) {
                imageUrl = savedPath
            }
        }
    }

    var editImageUrlState by remember { mutableStateOf("") }

    val editImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(context, it)
            if (savedPath != null) {
                editImageUrlState = savedPath
            }
        }
    }

    val products by viewModel.allProducts.collectAsState()

    var showEditDialog by remember { mutableStateOf<Product?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Moon Light Admin",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Role: Owner / Partner Gate",
                            color = LunarGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetAdminAccess()
                        onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LunarSurface)
            )
        },
        containerColor = MidnightBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            
            // Administrative Subheading Navigation Tabs
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = LunarSurface,
                contentColor = LunarGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = LunarGold
                    )
                }
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Inventory Catalog", fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.List, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Add Products", fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                if (activeTab == 0) {
                    // TAB 0: Current Products catalog
                    if (products.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = NeutralTextMuted, modifier = Modifier.size(60.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No Database Inventory Registered", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(products) { product ->
                                AdminProductCatalogRow(
                                    product = product,
                                    onEdit = { 
                                        showEditDialog = product
                                        editImageUrlState = product.imageUrl
                                    },
                                    onDelete = { showDeleteConfirmDialog = product }
                                )
                            }
                        }
                    }
                } else {
                    // TAB 1: Add product form
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = LunarSurface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Register New Product", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)

                                OutlinedTextField(
                                    value = title,
                                    onValueChange = { title = it },
                                    label = { Text("Product Title") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = LunarGold, focusedLabelColor = LunarGold
                                    )
                                )

                                OutlinedTextField(
                                    value = description,
                                    onValueChange = { description = it },
                                    label = { Text("Product Description Specifications") },
                                    minLines = 2,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                        focusedBorderColor = LunarGold, focusedLabelColor = LunarGold
                                    )
                                )

                                // Category selector row
                                Column {
                                    Text("Product Category", color = NeutralTextMuted, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                    Row(
                                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        val cats = listOf("Smart Decor", "Audio Essentials", "Cosmetics", "Gadgets", "Lifestyle & Sleep", "Apparel")
                                        cats.forEach { cat ->
                                            val isSelected = category == cat
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = { category = cat },
                                                label = { Text(cat, fontSize = 12.sp, color = if (isSelected) NeutralDark else NeutralTextLight) },
                                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = LunarGold, containerColor = LunarSurfaceLight)
                                            )
                                        }
                                    }
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    OutlinedTextField(
                                        value = originalPriceStr,
                                        onValueChange = { originalPriceStr = it },
                                        label = { Text("Original Price (Rs.)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                                    )
                                    OutlinedTextField(
                                        value = salePriceStr,
                                        onValueChange = { salePriceStr = it },
                                        label = { Text("Sale Price (Rs.)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                                    )
                                }

                                Text(
                                    text = "Product Picture",
                                    color = NeutralTextMuted,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .border(
                                            width = 1.dp,
                                            color = if (imageUrl.isNotBlank()) LunarGold else LunarSurfaceLight,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { imagePickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = LunarSurfaceLight)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (imageUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "Product preview",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .align(Alignment.BottomCenter)
                                                    .background(Color.Black.copy(alpha = 0.6f))
                                                    .padding(vertical = 8.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(Icons.Default.Collections, contentDescription = null, tint = LunarGold, modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Tap to Change Picture", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        } else {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Default.Image,
                                                    contentDescription = "Add Photo",
                                                    tint = LunarGold,
                                                    modifier = Modifier.size(48.dp)
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Tap to Choose Picture", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                                Text("Supports PNG, JPG, JPEG from Gallery", color = NeutralTextMuted, fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Marked as Hot Deals Promo?", color = Color.White, fontSize = 13.sp)
                                    Switch(
                                        checked = isHotDeal,
                                        onCheckedChange = { isHotDeal = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = LunarGold, checkedTrackColor = LunarSurfaceLight)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        val oPrice = originalPriceStr.toDoubleOrNull()
                                        val sPrice = salePriceStr.toDoubleOrNull()

                                        if (title.isBlank() || description.isBlank() || oPrice == null || sPrice == null) {
                                            formMessage = "Error: Please complete all fields with correct numbers."
                                            formSuccess = false
                                        } else {
                                            viewModel.addProduct(
                                                title = title,
                                                description = description,
                                                category = category,
                                                originalPrice = oPrice,
                                                salePrice = sPrice,
                                                imageUrl = imageUrl,
                                                isHotDeal = isHotDeal
                                            )
                                            formMessage = "Successfully registered product to Moon Light catalog!"
                                            formSuccess = true

                                            // Reset fields
                                            title = ""
                                            description = ""
                                            originalPriceStr = ""
                                            salePriceStr = ""
                                            imageUrl = ""
                                            isHotDeal = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = LunarGold, contentColor = NeutralDark)
                                ) {
                                    Icon(Icons.Default.Publish, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Publish Product", fontWeight = FontWeight.Bold)
                                }

                                if (formMessage.isNotBlank()) {
                                    Text(
                                        text = formMessage,
                                        color = if (formSuccess) LunarGold else SlashedPriceColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // PRODUCT EDITING MODAL (Dialog popup)
    showEditDialog?.let { currentProd ->
        var editTitle by remember { mutableStateOf(currentProd.title) }
        var editDescription by remember { mutableStateOf(currentProd.description) }
        var editCategory by remember { mutableStateOf(currentProd.category) }
        var editOriginalPrice by remember { mutableStateOf(currentProd.originalPrice.toString()) }
        var editSalePrice by remember { mutableStateOf(currentProd.salePrice.toString()) }
        var editIsHotDeal by remember { mutableStateOf(currentProd.isHotDeal) }

        var modalError by remember { mutableStateOf(false) }

        LaunchedEffect(currentProd) {
            editImageUrlState = currentProd.imageUrl
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = null },
            title = { Text("Edit Catalog Inventory", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                    )

                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Description") },
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                    )

                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = { editCategory = it },
                        label = { Text("Category") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                    )

                    OutlinedTextField(
                        value = editOriginalPrice,
                        onValueChange = { editOriginalPrice = it },
                        label = { Text("Original Price") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                    )

                    OutlinedTextField(
                        value = editSalePrice,
                        onValueChange = { editSalePrice = it },
                        label = { Text("Sale Price") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, focusedBorderColor = LunarGold)
                    )

                    Text(
                        text = "Product Picture",
                        color = NeutralTextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .border(
                                width = 1.dp,
                                color = if (editImageUrlState.isNotBlank()) LunarGold else LunarSurfaceLight,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { editImageLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = LunarSurfaceLight)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (editImageUrlState.isNotBlank()) {
                                AsyncImage(
                                    model = editImageUrlState,
                                    contentDescription = "Product preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .align(Alignment.BottomCenter)
                                        .background(Color.Black.copy(alpha = 0.6f))
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Collections, contentDescription = null, tint = LunarGold, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Tap to Change Picture", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Add Photo",
                                        tint = LunarGold,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Tap to Choose Picture", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Hot Deals status?", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = editIsHotDeal,
                            onCheckedChange = { editIsHotDeal = it }
                        )
                    }

                    if (modalError) {
                        Text("Please fill invalid fields.", color = SlashedPriceColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val oPrice = editOriginalPrice.toDoubleOrNull()
                        val sPrice = editSalePrice.toDoubleOrNull()
                        if (editTitle.isBlank() || editDescription.isBlank() || oPrice == null || sPrice == null) {
                            modalError = true
                        } else {
                            viewModel.editProduct(
                                currentProd.copy(
                                    title = editTitle,
                                    description = editDescription,
                                    category = editCategory,
                                    originalPrice = oPrice,
                                    salePrice = sPrice,
                                    imageUrl = editImageUrlState,
                                    isHotDeal = editIsHotDeal
                                )
                            )
                            showEditDialog = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LunarGold, contentColor = NeutralDark)
                ) {
                    Text("Apply Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = LunarSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // PRODUCT DELETION CONFIRMATION DIALOG (Spec 5 Delete products)
    showDeleteConfirmDialog?.let { currentProd ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Confirm Product Removal", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Text(
                    "Are you sure you want to permanently delete \"${currentProd.title}\"? This action cannot be undone and will instantly update of all customer downloaded applications.",
                    color = NeutralTextLight,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteProduct(currentProd)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SlashedPriceColor, contentColor = Color.White)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = LunarSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// INVENTORY ROW DISPLAY FOR DASHBOARD EDITOR
@Composable
fun AdminProductCatalogRow(
    product: Product,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LunarSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = product.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.category} | Rs. ${product.salePrice.toInt()}",
                    color = LunarGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Product", tint = LunarGold)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Product", tint = SlashedPriceColor)
                }
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.use { input ->
            val fileName = "prod_img_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
            file.absolutePath
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

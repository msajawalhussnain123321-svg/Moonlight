package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MoonLightLogo
import com.example.ui.theme.*
import com.example.ui.viewmodel.ECommerceViewModel

@Composable
fun LoginScreen(
    viewModel: ECommerceViewModel,
    onNavigateHome: () -> Unit
) {
    var showAccountPicker by remember { mutableStateOf(false) }
    var showCustomAccountInput by remember { mutableStateOf(false) }

    // Form inputs for custom sign on
    var customName by remember { mutableStateOf(TextFieldValue("")) }
    var customEmail by remember { mutableStateOf(TextFieldValue("")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MidnightBlue, Color(0xFF13172B))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Branding Top Header
            MoonLightLogo(
                showText = true,
                glowSize = 25f,
                textSize = 24f
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Text descriptor
            Text(
                text = "Sign in to unlock exclusive hot deals, track orders, and customize your experience in the official Moon Light Store.",
                color = NeutralTextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Spec 2: Google ID Login prominent styled button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .clickable { showAccountPicker = true }
                    .testTag("submit_button"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Styled custom vector representation of Google G Icon to keep dependencies clean and fully local
                    Canvas(modifier = Modifier.size(22.dp)) {
                        // Simple artistic high contrast Google Icon representation
                        drawCircle(
                            color = Color(0xFF4285F4),
                            radius = this.size.width / 2
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Sign in with Google",
                        color = Color(0xFF263238),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Spec 2 Guest Mode 'Skip for Now'
            TextButton(
                onClick = {
                    viewModel.skipLoginAsGuest()
                    onNavigateHome()
                },
                modifier = Modifier.testTag("skip_button")
            ) {
                Text(
                    text = "Skip for Now",
                    color = LunarGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // App ownership foot credits
            Text(
                text = "Muhammad Sajawal Hussnain & Miss Mano",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }

        // Account Selection Modal Sheets (Google Sign-In Account simulation)
        if (showAccountPicker) {
            AlertDialog(
                onDismissRequest = { showAccountPicker = false },
                title = {
                    Text(
                        text = "Choose Google Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "Choose an account to continue to Moon Light store",
                            color = NeutralTextMuted,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Option 1: Proprietor Profile
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.signInWithGoogle(
                                        name = "Muhammad Sajawal Hussnain",
                                        email = "markazapp123321@gmail.com",
                                        profilePicUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
                                    )
                                    showAccountPicker = false
                                    onNavigateHome()
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(LunarGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "MS",
                                    color = NeutralDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Muhammad Sajawal Hussnain",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "markazapp123321@gmail.com",
                                    color = NeutralTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Divider(color = LunarSurfaceLight, modifier = Modifier.padding(vertical = 8.dp))

                        // Option 2: Co-partner profile
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.signInWithGoogle(
                                        name = "Miss Mano",
                                        email = "mano.copartner@gmail.com",
                                        profilePicUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150"
                                    )
                                    showAccountPicker = false
                                    onNavigateHome()
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(DeepSkyBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "MM",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Miss Mano",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "mano.copartner@gmail.com",
                                    color = NeutralTextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Divider(color = LunarSurfaceLight, modifier = Modifier.padding(vertical = 8.dp))

                        // Option 3: Custom Login Mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    showCustomAccountInput = true
                                    showAccountPicker = false
                                }
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(LunarSurfaceLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add account",
                                    tint = LunarGold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Use another Google account",
                                color = LunarGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAccountPicker = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = LunarSurface,
                shape = RoundedCornerShape(20.dp)
            )
        }

        // Custom account creation popup if they select option 3
        if (showCustomAccountInput) {
            AlertDialog(
                onDismissRequest = { showCustomAccountInput = false },
                title = {
                    Text(
                        text = "Enter Account Details",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = customName,
                            onValueChange = { customName = it },
                            label = { Text("Display Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = LunarGold) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = LunarGold,
                                focusedLabelColor = LunarGold
                            )
                        )

                        OutlinedTextField(
                            value = customEmail,
                            onValueChange = { customEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = LunarGold) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = LunarGold,
                                focusedLabelColor = LunarGold
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val name = customName.text.ifBlank { "User Account" }
                            val email = customEmail.text.ifBlank { "customuser@gmail.com" }
                            viewModel.signInWithGoogle(
                                name = name,
                                email = email,
                                profilePicUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150"
                            )
                            showCustomAccountInput = false
                            onNavigateHome()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LunarGold, contentColor = NeutralDark)
                    ) {
                        Text("Connect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomAccountInput = false }) {
                        Text("Back", color = Color.White)
                    }
                },
                containerColor = LunarSurface,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

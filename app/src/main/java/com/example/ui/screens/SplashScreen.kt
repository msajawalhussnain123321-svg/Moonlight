package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MoonLightLogo
import com.example.ui.theme.LunarGold
import com.example.ui.theme.MidnightBlue
import com.example.ui.theme.NeutralTextMuted
import com.example.ui.viewmodel.ECommerceViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: ECommerceViewModel,
    onNavigateNext: (destination: String) -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val userSession by viewModel.userSession.collectAsState()

    LaunchedEffect(Unit) {
        visible = true
        delay(2200) // Beautiful splash hold time
        
        // Dynamic navigation routing based on local persistent credentials
        val currentSession = userSession
        if (currentSession != null && (currentSession.isLoggedIn || currentSession.isGuest)) {
            onNavigateNext("home")
        } else {
            onNavigateNext("login")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MidnightBlue,
                        Color(0xFF070911), // Extra deep midnight shade
                        MidnightBlue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(500))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                MoonLightLogo(
                    glowSize = 40f,
                    textSize = 28f
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "A Premium E-Commerce Experience",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(80.dp))

                // Official enterprise credits as requested in specification 1
                Text(
                    text = "PROPRIETOR",
                    color = LunarGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Muhammad Sajawal Hussnain",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CO-PARTNER",
                    color = LunarGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Miss Mano",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AuroraBackground
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable
import com.example.ui.screens.AddTransactionScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WalletsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize MVVM ViewModel via our Factory
        val viewModel: MainViewModel by viewModels {
            MainViewModel.Factory(application)
        }

        setContent {
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()
            val isDark by viewModel.isDarkMode.collectAsState()
            val selectedTab by viewModel.selectedTab.collectAsState()
            val isAddOpen by viewModel.isAddSheetOpen.collectAsState()
            val toastMessage by viewModel.toastMessage.collectAsState()
            
            val context = LocalContext.current

            // Show Toast notifications from flow triggers
            LaunchedEffect(toastMessage) {
                toastMessage?.let { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    viewModel.clearToast()
                }
            }

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Base colorful aurora background layer that shines through glass
                    AuroraBackground(isDark = isDark)

                    if (!isLoggedIn) {
                        LoginScreen(
                            viewModel = viewModel,
                            isDark = isDark
                        )
                    } else {
                        Scaffold(
                            containerColor = Color.Transparent, // let the aurora show underneath
                            modifier = Modifier.fillMaxSize(),
                            bottomBar = {
                                // Floating glass bottom bar dock
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    GlassCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        cornerRadius = 24.dp,
                                        isDark = isDark
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp, horizontal = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Tab 0: Dashboard (หน้าหลัก)
                                            NavBarItem(
                                                icon = Icons.Default.Home,
                                                label = "หน้าหลัก",
                                                isSelected = selectedTab == 0,
                                                isDark = isDark,
                                                onClick = { viewModel.selectTab(0) }
                                            )

                                            // Tab 1: Analytics (สถิติ)
                                            NavBarItem(
                                                icon = Icons.Default.ShowChart,
                                                label = "สถิติ",
                                                isSelected = selectedTab == 1,
                                                isDark = isDark,
                                                onClick = { viewModel.selectTab(1) }
                                            )

                                            // Center Spacer to prevent bottom dock items from overcrowding the central Floating FAB
                                            Spacer(modifier = Modifier.width(56.dp))

                                            // Tab 2: Budgets (งบประมาณ)
                                            NavBarItem(
                                                icon = Icons.Default.Star,
                                                label = "งบประมาณ",
                                                isSelected = selectedTab == 2,
                                                isDark = isDark,
                                                onClick = { viewModel.selectTab(2) }
                                            )

                                            // Tab 3: Wallets (กระเป๋าเงิน)
                                            NavBarItem(
                                                icon = Icons.Default.AccountBalanceWallet,
                                                label = "บัญชี",
                                                isSelected = selectedTab == 3,
                                                isDark = isDark,
                                                onClick = { viewModel.selectTab(3) }
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            // Render target active screen contents
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                            ) {
                                when (selectedTab) {
                                    0 -> DashboardScreen(
                                        viewModel = viewModel,
                                        isDark = isDark,
                                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                    )
                                    1 -> AnalyticsScreen(
                                        viewModel = viewModel,
                                        isDark = isDark,
                                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                    )
                                    2 -> BudgetsScreen(
                                        viewModel = viewModel,
                                        isDark = isDark,
                                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                    )
                                    3 -> WalletsScreen(
                                        viewModel = viewModel,
                                        isDark = isDark,
                                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                    )
                                    4 -> SettingsScreen(
                                        viewModel = viewModel,
                                        isDark = isDark,
                                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                                    )
                                }

                                // Symmetrical Floating Action Buttons for Settings & Add Dialog
                                // Settings Button (Top-Right Floating Glass Circle)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(16.dp)
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(
                                                alpha = 0.6f
                                            )
                                        )
                                        .jellyClickable { viewModel.selectTab(4) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "ตั้งค่า",
                                        tint = if (isDark) Color.White else Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        // Floating Glass Action Button (FAB) at Bottom-Center, overlapping the Navigation Bar elegantly
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(bottom = 26.dp) // Offset above the bottom dock bar
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF4F46E5), // Indigo 600
                                                Color(0xFF6366F1)  // Indigo 500
                                            )
                                        )
                                    )
                                    .jellyClickable(tag = "fab_add_transaction") { viewModel.setAddSheetOpen(true) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "เพิ่มรายการใหม่",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        // Add Transaction pop-up dialog with sleek liquid reveal fade and slide animation
                        AnimatedVisibility(
                            visible = isAddOpen,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { height -> height },
                                animationSpec = spring(dampingRatio = 0.75f, stiffness = 120f)
                            ),
                            exit = fadeOut() + slideOutVertically(
                                targetOffsetY = { height -> height },
                                animationSpec = spring(dampingRatio = 0.8f, stiffness = 150f)
                            )
                        ) {
                            AddTransactionScreen(
                                viewModel = viewModel,
                                isDark = isDark,
                                onDismiss = { viewModel.setAddSheetOpen(false) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NavBarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val activeColor = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5)
    val inactiveColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color(0xFF64748B)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(4.dp)
            .jellyClickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

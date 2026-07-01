package com.example.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WalletEntity
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable
import java.text.DecimalFormat

@Composable
fun WalletsScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val wallets by viewModel.wallets.collectAsState()
    val currencyFormatter = remember { DecimalFormat("#,##0.00") }

    // State for creating new wallet
    var walletNameInput by remember { mutableStateOf("") }
    var walletBalanceInput by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    val walletColors = listOf(
        "#FF8A80", // Coral Pink
        "#80D8FF", // Neon Cyan
        "#B9F6CA", // Mint Green
        "#FFE082", // Pastel Amber
        "#EA80FC"  // Soft Purple
    )

    // Overlapping state
    var expandedCardIndex by remember { mutableStateOf(-1) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "กระเป๋าเงินและบัญชี 💳",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1E293B)
                )
                Text(
                    text = "จัดการบัญชีของคุณ เลื่อนซ้าย-ขวาเพื่อดูบัญชีทั้งหมด",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B)
                )
            }
        }

        // 1. Sleek, Non-Overlapping horizontal scrollable wallet cards carousel (Clean Minimalism Layout)
        item {
            if (wallets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ไม่มีข้อมูลกระเป๋าเงิน",
                        color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    itemsIndexed(wallets) { index, wallet ->
                        WalletCardItem(
                            wallet = wallet,
                            isDark = isDark,
                            currencyFormatter = currencyFormatter,
                            modifier = Modifier
                                .width(300.dp)
                                .height(175.dp)
                        )
                    }
                }
            }
        }

        // Form to add a new account/wallet
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "เพิ่มบัญชี/กระเป๋าเงินใหม่ 💳",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Input wallet name
                    TextField(
                        value = walletNameInput,
                        onValueChange = { walletNameInput = it },
                        placeholder = { Text("ชื่อบัญชี เช่น บัญชีออมทรัพย์", color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
                            focusedTextColor = if (isDark) Color.White else Color.Black,
                            unfocusedTextColor = if (isDark) Color.White else Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Input starting balance
                    TextField(
                        value = walletBalanceInput,
                        onValueChange = { walletBalanceInput = it },
                        placeholder = { Text("ยอดเงินเริ่มต้น เช่น 10000", color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
                            focusedTextColor = if (isDark) Color.White else Color.Black,
                            unfocusedTextColor = if (isDark) Color.White else Color.Black,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Color swatches selector
                    Text(
                        text = "เลือกสีสีกระเป๋าเงิน",
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.DarkGray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        walletColors.forEachIndexed { i, colHex ->
                            val isSelected = selectedColorIndex == i
                            val c = Color(android.graphics.Color.parseColor(colHex))

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .border(
                                        2.dp,
                                        if (isSelected) (if (isDark) Color.White else Color.Black) else Color.Transparent,
                                        CircleShape
                                    )
                                    .jellyClickable { selectedColorIndex = i }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Set button
                    Button(
                        onClick = {
                            val startBal = walletBalanceInput.toDoubleOrNull() ?: 0.0
                            if (walletNameInput.isNotBlank()) {
                                viewModel.addNewWallet(
                                    name = walletNameInput,
                                    initialBalance = startBal,
                                    colorHex = walletColors[selectedColorIndex]
                                )
                                walletNameInput = ""
                                walletBalanceInput = ""
                            }
                        },
                        enabled = walletNameInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5), // Clean Minimalism Indigo 600
                            contentColor = Color.White,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .jellyClickable(enabled = walletNameInput.isNotBlank()) {
                                val startBal = walletBalanceInput.toDoubleOrNull() ?: 0.0
                                if (walletNameInput.isNotBlank()) {
                                    viewModel.addNewWallet(
                                        name = walletNameInput,
                                        initialBalance = startBal,
                                        colorHex = walletColors[selectedColorIndex]
                                    )
                                    walletNameInput = ""
                                    walletBalanceInput = ""
                                }
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "เพิ่มกระเป๋าเงิน", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("สร้างกระเป๋าเงินใหม่", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WalletCardItem(
    wallet: WalletEntity,
    isDark: Boolean,
    currencyFormatter: DecimalFormat,
    modifier: Modifier = Modifier
) {
    val cardColor = Color(android.graphics.Color.parseColor(wallet.colorHex))
    
    // Choose icon depending on name
    val walletIcon = when {
        wallet.name.contains("ธนาคาร") -> Icons.Default.AccountBalance
        wallet.name.contains("บัตร") -> Icons.Default.CreditCard
        wallet.name.contains("เงินสด") -> Icons.Default.Money
        else -> Icons.Default.AccountBalanceWallet
    }

    val primaryTextColor = if (isDark) Color.White else Color(0xFF0F172A)
    val secondaryTextColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF475569)
    val cardBgGradient = if (isDark) {
        Brush.linearGradient(
            colors = listOf(
                cardColor.copy(alpha = 0.35f),
                Color.Black.copy(alpha = 0.3f)
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                cardColor.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.85f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cardBgGradient)
            .border(
                width = 1.dp,
                color = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.60f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = walletIcon,
                        contentDescription = wallet.name,
                        tint = if (isDark) cardColor else cardColor.copy(alpha = 0.9f),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = wallet.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryTextColor
                    )
                }

                Text(
                    text = "GLASS PAY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = secondaryTextColor.copy(alpha = 0.5f)
                )
            }

            Column {
                Text(
                    text = "ยอดเงินในบัญชี",
                    fontSize = 11.sp,
                    color = secondaryTextColor
                )
                Text(
                    text = "฿${currencyFormatter.format(wallet.balance)}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryTextColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "•••• •••• •••• ${1000 + wallet.id}",
                    fontSize = 12.sp,
                    color = secondaryTextColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "09/29",
                    fontSize = 11.sp,
                    color = secondaryTextColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}

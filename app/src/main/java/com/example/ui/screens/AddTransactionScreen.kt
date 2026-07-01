package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.WalletEntity
import com.example.ui.MainViewModel
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wallets by viewModel.wallets.collectAsState()

    // Transaction parameters
    var transactionType by remember { mutableStateOf("expense") } // "income" or "expense"
    var amountString by remember { mutableStateOf("0") }
    var selectedCategory by remember { mutableStateOf("อาหาร") }
    var selectedWalletId by remember { mutableStateOf(1) } // Default cash
    var noteString by remember { mutableStateOf("") }

    // Synchronize selected wallet ID if wallets are loaded and 1 is not in them
    LaunchedEffect(wallets) {
        if (wallets.isNotEmpty() && wallets.none { it.id == selectedWalletId }) {
            selectedWalletId = wallets.first().id
        }
    }

    val categories = if (transactionType == "income") {
        listOf("เงินเดือน", "ลงทุน", "อื่นๆ")
    } else {
        listOf("อาหาร", "เดินทาง", "ช้อปปิ้ง", "บันเทิง", "อื่นๆ")
    }

    // Amount display calculations
    val amountVal = amountString.toDoubleOrNull() ?: 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF131522).copy(alpha = 0.95f) else Color(0xFFF4F7FA).copy(alpha = 0.98f))
            .statusBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Sheet Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "บันทึกรายการ 📝",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1C1B1F)
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                    .jellyClickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "ปิด",
                    tint = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 1. Segmented Buttons for Transaction Type
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isDark = isDark,
            cornerRadius = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "expense" to "รายจ่าย",
                    "income" to "รายรับ",
                    "transfer" to "โอนเงิน"
                ).forEach { (typeKey, titleText) ->
                    val isSelected = transactionType == typeKey
                    val targetBg = animateColorAsState(
                        targetValue = if (isSelected) {
                            when (typeKey) {
                                "income" -> Color(0xFF2E7D32).copy(alpha = 0.8f)
                                "expense" -> Color(0xFFC62828).copy(alpha = 0.8f)
                                else -> Color(0xFF1565C0).copy(alpha = 0.8f)
                            }
                        } else Color.Transparent,
                        label = "segmentBg"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(targetBg.value)
                            .jellyClickable {
                                transactionType = typeKey
                                // Reset selected category if not applicable
                                if (typeKey == "income") {
                                    selectedCategory = "เงินเดือน"
                                } else {
                                    selectedCategory = "อาหาร"
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = titleText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected || !isDark) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Liquid/Animated Amount Indicator
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            isDark = isDark,
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "จํานวนเงินที่บันทึก",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                // Smooth scaling digits
                val textScale = animateFloatAsState(
                    targetValue = if (amountString.length > 7) 24f else 36f,
                    animationSpec = spring(stiffness = 300f),
                    label = "textScale"
                )

                Text(
                    text = "฿ $amountString",
                    fontSize = textScale.value.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (transactionType == "income") Color(0xFF00E676) else if (transactionType == "expense") Color(0xFFFF5252) else Color(0xFF40C4FF),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Horizontal Row of Category Icons with spring selection expands
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "หมวดหมู่รายการ",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    val sizeScale = animateFloatAsState(
                        targetValue = if (isSelected) 1.15f else 1.0f,
                        animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
                        label = "categoryAnim"
                    )
                    val bgColor = if (isSelected) {
                        getCategoryColor(category).copy(alpha = 0.35f)
                    } else {
                        if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.60f)
                    }

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = sizeScale.value
                                scaleY = sizeScale.value
                            }
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(
                                1.dp,
                                if (isSelected) getCategoryColor(category) else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                            .jellyClickable { selectedCategory = category }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = getCategoryIcon(category),
                                contentDescription = category,
                                tint = if (isSelected) getCategoryColor(category) else (if (isDark) Color.White else Color.DarkGray),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color.Black
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Source Account/Wallet Choice Selector
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "ช่องทาง/กระเป๋าเงิน",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White.copy(alpha = 0.8f) else Color.DarkGray,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(wallets) { wallet ->
                    val isSelected = selectedWalletId == wallet.id
                    val bgColor = if (isSelected) {
                        Color(android.graphics.Color.parseColor(wallet.colorHex)).copy(alpha = 0.35f)
                    } else {
                        if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.60f)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(bgColor)
                            .border(
                                1.dp,
                                if (isSelected) Color(android.graphics.Color.parseColor(wallet.colorHex)) else Color.White.copy(alpha = 0.15f),
                                RoundedCornerShape(16.dp)
                            )
                            .jellyClickable { selectedWalletId = wallet.id }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${wallet.name} (฿${wallet.balance.toInt()})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Brief Note input field (Glass style)
        TextField(
            value = noteString,
            onValueChange = { noteString = it },
            placeholder = { Text("บันทึกช่วยจํา (ไม่ระบุ)...", color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray) },
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
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
            singleLine = true
        )

        Spacer(modifier = Modifier.weight(1f))

        // 5. Custom Minimalist Numeric Numpad with Jelly Keys
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf(".", "0", "⌫")
            )

            keys.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowKeys.forEach { key ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.70f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .jellyClickable {
                                    when (key) {
                                        "⌫" -> {
                                            if (amountString.isNotEmpty() && amountString != "0") {
                                                amountString = amountString.dropLast(1)
                                                if (amountString.isEmpty()) amountString = "0"
                                            }
                                        }
                                        "." -> {
                                            if (!amountString.contains(".")) {
                                                amountString += "."
                                            }
                                        }
                                        else -> {
                                            if (amountString == "0") {
                                                amountString = key
                                            } else {
                                                // Prevent entering more than 2 decimal places
                                                val dotIdx = amountString.indexOf('.')
                                                if (dotIdx == -1 || amountString.length - dotIdx <= 2) {
                                                    amountString += key
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (key == "⌫") {
                                Icon(
                                    imageVector = Icons.Default.Backspace,
                                    contentDescription = "ลบตัวเลข",
                                    tint = if (isDark) Color.White else Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = key,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Large Action Submit Button "บันทึก"
        Button(
            onClick = {
                if (amountVal > 0.0) {
                    viewModel.addTransaction(
                        type = transactionType,
                        amount = amountVal,
                        category = selectedCategory,
                        walletId = selectedWalletId,
                        note = noteString
                    )
                    onDismiss()
                }
            },
            enabled = amountVal > 0.0,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (transactionType == "income") Color(0xFF00E676) else if (transactionType == "expense") Color(0xFFFF5252) else Color(0xFF00B0FF),
                disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .jellyClickable(enabled = amountVal > 0.0, onClick = {
                    if (amountVal > 0.0) {
                        viewModel.addTransaction(
                            type = transactionType,
                            amount = amountVal,
                            category = selectedCategory,
                            walletId = selectedWalletId,
                            note = noteString
                        )
                        onDismiss()
                    }
                })
        ) {
            Text(
                text = "บันทึกรายการ 💾",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

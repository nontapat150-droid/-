package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable
import java.text.DecimalFormat

@Composable
fun BudgetsScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val budgets by viewModel.budgets.collectAsState()
    val currencyFormatter = remember { DecimalFormat("#,##0") }

    // Forms states for setting budget limit
    var selectedCategory by remember { mutableStateOf("อาหาร") }
    var limitInput by remember { mutableStateOf("") }
    val categories = listOf("อาหาร", "เดินทาง", "ช้อปปิ้ง", "บันเทิง", "อื่นๆ")

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
                    text = "งบประมาณ 🎯",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1C1B1F)
                )
                Text(
                    text = "วางแผนและจํากัดรายจ่ายเพื่อการประหยัดที่ดีขึ้น",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF49454F)
                )
            }
        }

        // List budgets
        if (budgets.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ยังไม่มีงบประมาณที่ตั้งไว้",
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(budgets, key = { it.id }) { budget ->
                val ratio = if (budget.limitAmount > 0) (budget.spentAmount / budget.limitAmount).toFloat() else 0f
                val isNearLimit = ratio >= 0.85f

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryColor(budget.category))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "งบประมาณ: ${budget.category}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }

                            if (isNearLimit) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "เตือนภัย",
                                        tint = Color(0xFFFF5252),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "ใกล้เต็มขีดจำกัด!",
                                        color = Color(0xFFFF5252),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Liquid Progress Bar simulating water levels
                        LiquidProgressBar(
                            progress = ratio,
                            isWarning = isNearLimit
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ใช้ไปแล้ว ฿${currencyFormatter.format(budget.spentAmount)}",
                                fontSize = 12.sp,
                                color = if (isNearLimit) Color(0xFFFF5252) else (if (isDark) Color.White.copy(alpha = 0.7f) else Color.DarkGray),
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "จํากัดที่ ฿${currencyFormatter.format(budget.limitAmount)}",
                                fontSize = 12.sp,
                                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // Add/Set Budget Limit Card
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
                        text = "กําหนดงบประมาณใหม่ 🎯",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Row of categories selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSelected = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) getCategoryColor(cat).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f))
                                    .border(
                                        1.dp,
                                        if (isSelected) getCategoryColor(cat) else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .jellyClickable { selectedCategory = cat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Input Limit
                    TextField(
                        value = limitInput,
                        onValueChange = { limitInput = it },
                        placeholder = { Text("ระบุวงเงินจํากัด เช่น 5000", color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray) },
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Set button
                    Button(
                        onClick = {
                            val limitVal = limitInput.toDoubleOrNull() ?: 0.0
                            if (limitVal > 0.0) {
                                viewModel.addNewBudget(selectedCategory, limitVal)
                                limitInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F46E5), // Indigo 600
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .jellyClickable {
                                val limitVal = limitInput.toDoubleOrNull() ?: 0.0
                                if (limitVal > 0.0) {
                                    viewModel.addNewBudget(selectedCategory, limitVal)
                                    limitInput = ""
                                }
                            }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "เพิ่มงบประมาณ", tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("กําหนดงบประมาณ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidProgressBar(
    progress: Float,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 100f),
        label = "WaterLevelAnim"
    )

    val liquidColor = if (isWarning) {
        Color(0xFFFF5252) // warning level (red tide)
    } else {
        Color(0xFF4F46E5) // Clean Minimalism Indigo 600
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
    ) {
        // Horizontal fluid gradient representing elastic liquid contents
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress.value)
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            liquidColor.copy(alpha = 0.60f),
                            liquidColor
                        )
                    )
                )
        )
    }
}

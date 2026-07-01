package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import java.text.DecimalFormat

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.transactions.collectAsState()
    val currencyFormatter = remember { DecimalFormat("#,##0.00") }

    // Aggregate expenses by category
    val expenses = transactions.filter { it.type == "expense" }
    val totalExpenseSum = expenses.sumOf { it.amount }
    val categoryTotals = expenses
        .groupBy { it.category }
        .mapValues { it.value.sumOf { trans -> trans.amount } }
        .toList()
        .sortedByDescending { it.second }

    // Animation for growing bars
    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

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
                    text = "สถิติและรายงาน 📊",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1C1B1F)
                )
                Text(
                    text = "วิเคราะห์พฤติกรรมการใช้จ่ายของคุณ",
                    fontSize = 14.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF49454F)
                )
            }
        }

        // Summary Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "รายจ่ายรวมทั้งหมด",
                        fontSize = 14.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "฿${currencyFormatter.format(totalExpenseSum)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF5252)
                    )
                }
            }
        }

        // Animated Bar Chart Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "กราฟสรุปรายจ่ายรายหมวดหมู่",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White else Color.Black
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (categoryTotals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ยังไม่มีข้อมูลบันทึกรายจ่าย",
                                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        // Custom growing bar chart drawing
                        val maxVal = categoryTotals.maxOf { it.second }.toFloat()
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            
                            val barCount = categoryTotals.size
                            val spaceBetween = 24.dp.toPx()
                            val totalSpaces = spaceBetween * (barCount - 1)
                            val barWidth = (canvasWidth - totalSpaces) / barCount

                            categoryTotals.forEachIndexed { index, (category, amount) ->
                                val barHeight = (amount.toFloat() / maxVal) * canvasHeight * 0.85f * animationProgress.value
                                val xOffset = index * (barWidth + spaceBetween)
                                val yOffset = canvasHeight - barHeight

                                // Draw bar shadow/track
                                drawRoundRect(
                                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                    topLeft = Offset(xOffset, 0f),
                                    size = Size(barWidth, canvasHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )

                                // Draw active bar with category gradient color
                                drawRoundRect(
                                    color = getCategoryColor(category),
                                    topLeft = Offset(xOffset, yOffset),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx())
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Labels Legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            categoryTotals.forEach { (category, _) ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(getCategoryColor(category))
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = category,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.DarkGray,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Percentage breakdown Title
        item {
            Text(
                text = "สัดส่วนแบ่งหมวดหมู่ค่าใช้จ่าย 🍰",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1C1B1F),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Percentage breakdown items
        if (categoryTotals.isEmpty()) {
            item {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ไม่มีข้อมูลหมวดหมู่",
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(categoryTotals) { (category, amount) ->
                val percentage = if (totalExpenseSum > 0) (amount / totalExpenseSum * 100).toInt() else 0

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    cornerRadius = 16.dp,
                    isDark = isDark
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(getCategoryColor(category).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(getCategoryColor(category))
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = category,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color.White else Color.Black
                                )
                                Text(
                                    text = "$percentage% ของรายจ่ายรวม",
                                    fontSize = 12.sp,
                                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                                )
                            }
                        }

                        Text(
                            text = "฿${currencyFormatter.format(amount)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

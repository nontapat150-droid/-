package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransactionEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val profileAvatarIndex by viewModel.profileAvatarIndex.collectAsState()

    val transactions by viewModel.transactions.collectAsState()
    val wallets by viewModel.wallets.collectAsState()

    // Calculations
    val totalBalance = wallets.sumOf { it.balance }
    
    // Simple logic for Today's Income / Expense
    val todayStart = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayTransactions = transactions.filter { it.timestamp >= todayStart }
    val todayIncome = todayTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val todayExpense = todayTransactions.filter { it.type == "expense" }.sumOf { it.amount }

    val currencyFormatter = remember { DecimalFormat("#,##0.00") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ยินดีต้อนรับ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color(0xFF1E293B)
                    )
                }

                // Interactive Minimalist Profile Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .jellyClickable {
                            viewModel.selectTab(4) // Direct navigation to settings/profile
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AvatarView(
                        avatarIndex = profileAvatarIndex,
                        size = 46.dp
                    )
                }
            }
        }

        // Glass balance card
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
                        text = "ยอดเงินคงเหลือ",
                        fontSize = 14.sp,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF475569),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "฿${currencyFormatter.format(totalBalance)}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color.White else Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Income Sub-card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.5f))
                                .border(
                                    width = 1.dp,
                                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFD1FAE5)), // emerald-100
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = "รายรับ",
                                        tint = Color(0xFF059669), // emerald-600
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "รายรับ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "฿${currencyFormatter.format(todayIncome)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669) // emerald-600
                                    )
                                }
                            }
                        }

                        // Expense Sub-card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) Color.White.copy(alpha = 0.04f) else Color.White.copy(alpha = 0.5f))
                                .border(
                                    width = 1.dp,
                                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFFE4E6)), // rose-100
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = "รายจ่าย",
                                        tint = Color(0xFFE11D48), // rose-600
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "รายจ่าย",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "฿${currencyFormatter.format(todayExpense)}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE11D48) // rose-600
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Expense categories donut chart card
        item {
            val expenseMap = transactions
                .filter { it.type == "expense" }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "หมวดหมู่ค่าใช้จ่าย",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF1E293B)
                        )
                        Text(
                            text = "ดูสถิติ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF818CF8) else Color(0xFF4F46E5),
                            modifier = Modifier
                                .jellyClickable { viewModel.selectTab(1) }
                                .padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (expenseMap.isEmpty()) {
                        Text(
                            text = "ยังไม่มีข้อมูลรายจ่ายในขณะนี้",
                            fontSize = 14.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            DonutChart(
                                data = expenseMap,
                                modifier = Modifier.size(140.dp)
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                expenseMap.keys.take(5).forEach { category ->
                                    val amount = expenseMap[category] ?: 0.0
                                    val color = getCategoryColor(category)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(color)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "$category: ฿${currencyFormatter.format(amount)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF1C1B1F),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Transactions Title
        item {
            Text(
                text = "รายการล่าสุด",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color.White else Color(0xFF1C1B1F),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Transactions list
        if (transactions.isEmpty()) {
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
                            text = "ไม่มีรายการบันทึก แตะ '+' เพื่อเพิ่มรายการ",
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(transactions.take(10), key = { it.id }) { item ->
                var isVisible by remember { mutableStateOf(true) }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    TransactionItem(
                        transaction = item,
                        isDark = isDark,
                        onDelete = {
                            isVisible = false
                            viewModel.deleteTransaction(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    isDark: Boolean,
    onDelete: () -> Unit
) {
    val currencyFormatter = remember { DecimalFormat("#,##0.00") }
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale("th", "TH")) }
    val formattedDate = dateFormatter.format(Date(transaction.timestamp))

    val isExpense = transaction.type == "expense"
    val colorAccent = if (isExpense) Color(0xFFFF8A80) else Color(0xFFB9F6CA)
    val amountPrefix = if (isExpense) "-" else "+"

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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colorAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(transaction.category),
                    contentDescription = transaction.category,
                    tint = if (isExpense) Color(0xFFFF5252) else Color(0xFF00E676),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.note.isNotBlank()) transaction.note else transaction.category,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1C1B1F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$formattedDate • ${transaction.category}",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$amountPrefix฿${currencyFormatter.format(transaction.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) Color(0xFFFF5252) else Color(0xFF00E676),
                    modifier = Modifier.padding(end = 8.dp)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "ลบ",
                        tint = Color.Red.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DonutChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val entries = data.toList().sortedByDescending { it.second }
    val total = entries.sumOf { it.second }.toFloat()

    val scaleAnimate = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 100),
        label = "ChartAnim"
    )

    Canvas(modifier = modifier) {
        var startAngle = -90f
        val strokeWidth = 32.dp.toPx()

        entries.forEach { (category, amount) ->
            val sweepAngle = (amount.toFloat() / total) * 360f * scaleAnimate.value
            val color = getCategoryColor(category)

            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            startAngle += sweepAngle
        }
    }
}

fun getCategoryColor(category: String): Color {
    return when (category) {
        "อาหาร" -> Color(0xFFFFAB91)       // Coral
        "เดินทาง" -> Color(0xFF81D4FA)     // Sky Blue
        "ช้อปปิ้ง" -> Color(0xFFC5E1A5)     // Light Green
        "บันเทิง" -> Color(0xFFE1BEE7)     // Lilac
        "เงินเดือน" -> Color(0xFFA5D6A7)    // Soft Green
        "ลงทุน" -> Color(0xFFFFF59D)       // Soft Yellow
        else -> Color(0xFFFFE082)         // Golden/Amber
    }
}

fun getCategoryIcon(category: String): ImageVector {
    return when (category) {
        "อาหาร" -> Icons.Default.Fastfood
        "เดินทาง" -> Icons.Default.DirectionsBus
        "ช้อปปิ้ง" -> Icons.Default.LocalMall
        "บันเทิง" -> Icons.Default.SportsEsports
        "เงินเดือน", "ลงทุน" -> Icons.Default.MonetizationOn
        else -> Icons.Default.MoreHoriz
    }
}

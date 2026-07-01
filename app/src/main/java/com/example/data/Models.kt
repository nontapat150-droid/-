package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "income" (รายรับ), "expense" (รายจ่าย), "transfer" (โอนเงิน)
    val amount: Double,
    val category: String,
    val walletId: Int,
    val note: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // "เงินสด", "บัญชีธนาคาร", "บัตรเครดิต"
    val balance: Double,
    val colorHex: String // e.g. "#4CAF50", "#2196F3", "#F44336"
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // e.g. "อาหาร", "เดินทาง", "ช้อปปิ้ง", "บันเทิง", "อื่นๆ"
    val limitAmount: Double,
    val spentAmount: Double
)

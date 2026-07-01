package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allWallets: Flow<List<WalletEntity>> = dao.getAllWallets()
    val allBudgets: Flow<List<BudgetEntity>> = dao.getAllBudgets()

    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        val wallets = dao.getAllWallets().first()
        if (wallets.isEmpty()) {
            // Seed initial wallets
            dao.insertWallet(WalletEntity(id = 1, name = "เงินสด", balance = 5000.0, colorHex = "#FF8A80")) // Coral pink
            dao.insertWallet(WalletEntity(id = 2, name = "บัญชีธนาคาร", balance = 45000.0, colorHex = "#80D8FF")) // Light Blue
            dao.insertWallet(WalletEntity(id = 3, name = "บัตรเครดิต", balance = -2500.0, colorHex = "#B9F6CA")) // Light Green

            // Seed initial budgets
            dao.insertBudget(BudgetEntity(id = 1, category = "อาหาร", limitAmount = 6000.0, spentAmount = 1850.0))
            dao.insertBudget(BudgetEntity(id = 2, category = "เดินทาง", limitAmount = 2500.0, spentAmount = 680.0))
            dao.insertBudget(BudgetEntity(id = 3, category = "ช้อปปิ้ง", limitAmount = 8000.0, spentAmount = 5400.0))
            dao.insertBudget(BudgetEntity(id = 4, category = "บันเทิง", limitAmount = 3000.0, spentAmount = 2100.0))
            dao.insertBudget(BudgetEntity(id = 5, category = "อื่นๆ", limitAmount = 2000.0, spentAmount = 350.0))

            // Seed some initial transactions
            dao.insertTransaction(
                TransactionEntity(
                    id = 1,
                    type = "income",
                    amount = 12000.0,
                    category = "เงินเดือน",
                    walletId = 2,
                    note = "เงินเดือนประจํา",
                    timestamp = System.currentTimeMillis() - 86400000L * 2
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    id = 2,
                    type = "expense",
                    amount = 120.0,
                    category = "อาหาร",
                    walletId = 1,
                    note = "อาหารกลางวัน",
                    timestamp = System.currentTimeMillis() - 3600000L * 4
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    id = 3,
                    type = "expense",
                    amount = 450.0,
                    category = "เดินทาง",
                    walletId = 1,
                    note = "ค่าบริการรถโดยสาร",
                    timestamp = System.currentTimeMillis() - 3600000L * 2
                )
            )
            dao.insertTransaction(
                TransactionEntity(
                    id = 4,
                    type = "expense",
                    amount = 1500.0,
                    category = "ช้อปปิ้ง",
                    walletId = 3,
                    note = "เสื้อผ้าแฟชั่น",
                    timestamp = System.currentTimeMillis() - 1800000L
                )
            )
        }
    }

    suspend fun insertTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        // 1. Insert the transaction
        dao.insertTransaction(transaction)

        // 2. Adjust corresponding wallet balance
        val walletsList = dao.getAllWallets().first()
        val targetWallet = walletsList.find { it.id == transaction.walletId }
        if (targetWallet != null) {
            val updatedBalance = when (transaction.type) {
                "income" -> targetWallet.balance + transaction.amount
                "expense" -> targetWallet.balance - transaction.amount
                else -> targetWallet.balance // Transfer logic could be added
            }
            dao.updateWallet(targetWallet.copy(balance = updatedBalance))
        }

        // 3. Adjust corresponding budget if type is expense
        if (transaction.type == "expense") {
            val budgetsList = dao.getAllBudgets().first()
            val targetBudget = budgetsList.find { it.category == transaction.category }
            if (targetBudget != null) {
                val updatedSpent = targetBudget.spentAmount + transaction.amount
                dao.updateBudget(targetBudget.copy(spentAmount = updatedSpent))
            } else {
                // If budget for this category doesn't exist, we can optionally create one with default limit
                dao.insertBudget(
                    BudgetEntity(
                        category = transaction.category,
                        limitAmount = 5000.0,
                        spentAmount = transaction.amount
                    )
                )
            }
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        dao.deleteTransaction(transaction)

        // Revert wallet balances
        val walletsList = dao.getAllWallets().first()
        val targetWallet = walletsList.find { it.id == transaction.walletId }
        if (targetWallet != null) {
            val updatedBalance = when (transaction.type) {
                "income" -> targetWallet.balance - transaction.amount
                "expense" -> targetWallet.balance + transaction.amount
                else -> targetWallet.balance
            }
            dao.updateWallet(targetWallet.copy(balance = updatedBalance))
        }

        // Revert budget spent
        if (transaction.type == "expense") {
            val budgetsList = dao.getAllBudgets().first()
            val targetBudget = budgetsList.find { it.category == transaction.category }
            if (targetBudget != null) {
                val updatedSpent = maxOf(0.0, targetBudget.spentAmount - transaction.amount)
                dao.updateBudget(targetBudget.copy(spentAmount = updatedSpent))
            }
        }
    }

    suspend fun addWallet(wallet: WalletEntity) = withContext(Dispatchers.IO) {
        dao.insertWallet(wallet)
    }

    suspend fun addBudget(budget: BudgetEntity) = withContext(Dispatchers.IO) {
        dao.insertBudget(budget)
    }
}

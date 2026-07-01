package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BudgetEntity
import com.example.data.TransactionEntity
import com.example.data.TransactionRepository
import com.example.data.WalletEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val prefs = application.getSharedPreferences("user_profile_prefs", android.content.Context.MODE_PRIVATE)

    // User authentication & profile states
    private val _isLoggedIn = MutableStateFlow(prefs.getBoolean("is_logged_in", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow(prefs.getString("user_name", "ผู้ใช้งาน") ?: "ผู้ใช้งาน")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow(prefs.getString("user_email", "user@example.com") ?: "user@example.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profileAvatarIndex = MutableStateFlow(prefs.getInt("profile_avatar_index", 0))
    val profileAvatarIndex: StateFlow<Int> = _profileAvatarIndex.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TransactionRepository(database.transactionDao())
        
        // Populate default data on app launch
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    val transactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val wallets: StateFlow<List<WalletEntity>> = repository.allWallets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val budgets: StateFlow<List<BudgetEntity>> = repository.allBudgets
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // UI States
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _isAddSheetOpen = MutableStateFlow(false)
    val isAddSheetOpen: StateFlow<Boolean> = _isAddSheetOpen.asStateFlow()

    // Settings States
    private val _isDarkMode = MutableStateFlow(true) // Default to dark for premium glassmorphism
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _isReminderEnabled = MutableStateFlow(true)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setAddSheetOpen(isOpen: Boolean) {
        _isAddSheetOpen.value = isOpen
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    fun toggleReminder() {
        _isReminderEnabled.value = !_isReminderEnabled.value
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun addTransaction(type: String, amount: Double, category: String, walletId: Int, note: String) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    type = type,
                    amount = amount,
                    category = category,
                    walletId = walletId,
                    note = note,
                    timestamp = System.currentTimeMillis()
                )
            )
            _toastMessage.value = "บันทึกรายการเรียบร้อยแล้ว!"
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            _toastMessage.value = "ลบรายการเรียบร้อยแล้ว!"
        }
    }

    fun addNewWallet(name: String, initialBalance: Double, colorHex: String) {
        viewModelScope.launch {
            repository.addWallet(
                WalletEntity(
                    name = name,
                    balance = initialBalance,
                    colorHex = colorHex
                )
            )
            _toastMessage.value = "เพิ่มกระเป๋าเงินใหม่แล้ว!"
        }
    }

    fun addNewBudget(category: String, limit: Double) {
        viewModelScope.launch {
            repository.addBudget(
                BudgetEntity(
                    category = category,
                    limitAmount = limit,
                    spentAmount = 0.0
                )
            )
            _toastMessage.value = "เพิ่มงบประมาณหมวดหมู่ใหม่แล้ว!"
        }
    }

    fun login(name: String, email: String, avatarIndex: Int) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_name", name.trim())
            .putString("user_email", email.trim())
            .putInt("profile_avatar_index", avatarIndex)
            .apply()

        _userName.value = name.trim()
        _userEmail.value = email.trim()
        _profileAvatarIndex.value = avatarIndex
        _isLoggedIn.value = true
        _toastMessage.value = "ยินดีต้อนรับคุณ $name! 🎉"
    }

    fun logout() {
        prefs.edit()
            .putBoolean("is_logged_in", false)
            .apply()
        _isLoggedIn.value = false
        _toastMessage.value = "ลงชื่อออกเรียบร้อยแล้ว"
    }

    fun updateProfile(name: String, email: String, avatarIndex: Int) {
        prefs.edit()
            .putString("user_name", name.trim())
            .putString("user_email", email.trim())
            .putInt("profile_avatar_index", avatarIndex)
            .apply()

        _userName.value = name.trim()
        _userEmail.value = email.trim()
        _profileAvatarIndex.value = avatarIndex
        _toastMessage.value = "อัปเดตโปรไฟล์เรียบร้อยแล้ว!"
    }

    // Factory for manual initialization
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.getAvatarData
import com.example.ui.components.GlassCard
import com.example.ui.components.jellyClickable

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkModeState by viewModel.isDarkMode.collectAsState()
    val isReminderState by viewModel.isReminderEnabled.collectAsState()

    // Profile States
    val userName by viewModel.userName.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val profileAvatarIndex by viewModel.profileAvatarIndex.collectAsState()

    var showExportDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Dialog form states
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editAvatarIndex by remember { mutableStateOf(0) }

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
                    text = "ข้อมูลผู้ใช้งาน & การตั้งค่า ⚙️",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1C1B1F)
                )
                Text(
                    text = "จัดการข้อมูลบัญชีผู้ใช้และสไตล์การแสดงผลของระบบ",
                    fontSize = 13.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF49454F)
                )
            }
        }

        // 0. Profile Summary Card (Interactive)
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarView(
                        avatarIndex = profileAvatarIndex,
                        size = 64.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Text(
                            text = userEmail,
                            fontSize = 12.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "แก้ไขโปรไฟล์ ✏️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80D8FF),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .jellyClickable {
                                    editName = userName
                                    editEmail = userEmail
                                    editAvatarIndex = profileAvatarIndex
                                    showEditDialog = true
                                }
                        )
                    }
                }
            }
        }

        // 1. Setting Card Group
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    // Dark Mode row
                    SettingRow(
                        icon = Icons.Default.DarkMode,
                        title = "โหมดมืด",
                        description = "เปลี่ยนรูปแบบสีการแสดงผลเป็นสีทึบเพื่อสุขภาพตา",
                        isDark = isDark,
                        control = {
                            Switch(
                                checked = isDarkModeState,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF80D8FF),
                                    checkedTrackColor = Color(0xFF80D8FF).copy(alpha = 0.4f),
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    )

                    Divider(
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Reminder row
                    SettingRow(
                        icon = Icons.Default.Notifications,
                        title = "การแจ้งเตือน",
                        description = "รับการแจ้งเตือนรายวันเพื่อไม่พลาดการบันทึกรายการ",
                        isDark = isDark,
                        control = {
                            Switch(
                                checked = isReminderState,
                                onCheckedChange = { viewModel.toggleReminder() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF80D8FF),
                                    checkedTrackColor = Color(0xFF80D8FF).copy(alpha = 0.4f),
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    )

                    Divider(
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Export Data row
                    SettingRow(
                        icon = Icons.Default.Share,
                        title = "ส่งออกข้อมูล",
                        description = "ดาวน์โหลดรายงานบันทึกทั้งหมดเป็นไฟล์ CSV",
                        isDark = isDark,
                        control = {
                            Button(
                                onClick = { showExportDialog = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    contentColor = if (isDark) Color.White else Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.jellyClickable { showExportDialog = true }
                            ) {
                                Text("ส่งออก 💾", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    Divider(
                        color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.08f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Logout row
                    SettingRow(
                        icon = Icons.Default.Info,
                        title = "ออกจากระบบ",
                        description = "ออกจากบัญชีผู้ใช้ปัจจุบันเพื่อเปลี่ยนโปรไฟล์การเงิน",
                        isDark = isDark,
                        control = {
                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFF5252).copy(alpha = 0.15f),
                                    contentColor = Color(0xFFFF5252)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.jellyClickable { viewModel.logout() }
                            ) {
                                Text("ออกจากระบบ 🚪", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        }

        // 2. Info / Credits Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "ข้อมูล",
                        tint = if (isDark) Color(0xFF80D8FF) else Color(0xFF00B0FF),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "กระเป๋าเงินแก้วเวอร์ชัน 1.0.0",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color.Black
                        )
                        Text(
                            text = "ออกแบบด้วยหัวใจเพื่อความหรูหราของระบบคุณ 🌸",
                            fontSize = 11.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray
                        )
                    }
                }
            }
        }
    }

    // Success dialog for CSV export
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            confirmButton = {
                TextButton(
                    onClick = { showExportDialog = false },
                    modifier = Modifier.jellyClickable { showExportDialog = false }
                ) {
                    Text("ตกลง 👌", color = Color(0xFF80D8FF), fontWeight = FontWeight.Bold)
                }
            },
            title = {
                Text(
                    text = "ส่งออกข้อมูลสําเร็จ! 🎉",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "ข้อมูลรายรับรายจ่ายทั้งหมดของคุณได้ถูกแปลงและส่งออกเป็นไฟล์รายงาน 'glass_transactions_report.csv' ไปยังกล่องดาวน์โหลดเครื่องคุณเรียบร้อยแล้ว!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            },
            containerColor = Color(0xFF1B1D30),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
        )
    }

    // Edit profile dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val validName = if (editName.isBlank()) "ผู้ใช้งาน" else editName
                        val validEmail = if (editEmail.isBlank()) "user@example.com" else editEmail
                        viewModel.updateProfile(validName, validEmail, editAvatarIndex)
                        showEditDialog = false
                    },
                    modifier = Modifier.jellyClickable {
                        val validName = if (editName.isBlank()) "ผู้ใช้งาน" else editName
                        val validEmail = if (editEmail.isBlank()) "user@example.com" else editEmail
                        viewModel.updateProfile(validName, validEmail, editAvatarIndex)
                        showEditDialog = false
                    }
                ) {
                    Text("บันทึก 💾", color = Color(0xFF80D8FF), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false },
                    modifier = Modifier.jellyClickable { showEditDialog = false }
                ) {
                    Text("ยกเลิก", color = Color.Gray)
                }
            },
            title = {
                Text(
                    text = "แก้ไขข้อมูลผู้ใช้งาน ✏️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("ชื่อผู้ใช้งาน") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80D8FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFF80D8FF),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("อีเมลผู้ใช้งาน") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF80D8FF),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                            focusedLabelColor = Color(0xFF80D8FF),
                            unfocusedLabelColor = Color.White.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "เลือกภาพโปรไฟล์",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..4) {
                            val isSelected = editAvatarIndex == i
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clickable { editAvatarIndex = i }
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) Color(0xFF80D8FF) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .padding(if (isSelected) 2.dp else 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarView(
                                    avatarIndex = i,
                                    size = 36.dp
                                )
                            }
                        }
                    }
                }
            },
            containerColor = Color(0xFF1B1D30),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
        )
    }
}

@Composable
fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isDark: Boolean,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDark) Color.White else Color.Black,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color.Black
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Gray,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }
        }

        Box(modifier = Modifier.wrapContentSize()) {
            control()
        }
    }
}

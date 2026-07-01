package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.AvatarView
import com.example.ui.components.GlassCard
import com.example.ui.components.getAvatarData
import com.example.ui.components.jellyClickable

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedAvatarIndex by remember { mutableStateOf(0) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            isDark = isDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Logo Idea (Wallet Card overlap)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4F46E5), // Indigo 600
                                    Color(0xFF818CF8)  // Indigo 400
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Wallet lock",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Heading
                Text(
                    text = "ลงชื่อเข้าใช้งาน",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isDark) Color.White else Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "เริ่มต้นจัดการบันทึกรายรับรายจ่ายของคุณในแบบโปร่งแสง",
                    fontSize = 12.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.55f) else Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username input field
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("ชื่อผู้ใช้งาน") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Icon",
                            tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.LightGray,
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("username_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                // Email input field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("อีเมลผู้ใช้งาน") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDark) Color.White else Color.Black,
                        unfocusedTextColor = if (isDark) Color.White else Color.Black,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.LightGray,
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("email_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    )
                )

                // Avatar Selection Grid
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "เลือกภาพโปรไฟล์การเงินของคุณ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0..4) {
                            val isSelected = selectedAvatarIndex == i
                            val borderScale = animateFloatAsState(
                                targetValue = if (isSelected) 1.15f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
                                label = "borderScale"
                            )

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable { selectedAvatarIndex = i }
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) Color(0xFF818CF8) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .padding(if (isSelected) 3.dp else 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AvatarView(
                                    avatarIndex = i,
                                    size = 40.dp,
                                    modifier = Modifier.testTag("avatar_option_$i")
                                )
                            }
                        }
                    }

                    // Selected avatar tag label
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ประเภทโปรไฟล์: ${getAvatarData(selectedAvatarIndex).label}",
                        fontSize = 11.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit Button
                Button(
                    onClick = {
                        val validName = if (username.isBlank()) "ผู้ใช้งานทั่วไป" else username
                        val validEmail = if (email.isBlank()) "user@example.com" else email
                        viewModel.login(validName, validEmail, selectedAvatarIndex)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4F46E5),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .jellyClickable {
                            val validName = if (username.isBlank()) "ผู้ใช้งานทั่วไป" else username
                            val validEmail = if (email.isBlank()) "user@example.com" else email
                            viewModel.login(validName, validEmail, selectedAvatarIndex)
                        }
                        .testTag("login_submit_button")
                ) {
                    Text(
                        text = "เข้าสู่ระบบ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

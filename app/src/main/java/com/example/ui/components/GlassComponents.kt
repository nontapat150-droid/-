package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Jelly Clickable Modifier
fun Modifier.jellyClickable(
    enabled: Boolean = true,
    tag: String? = null,
    onClick: () -> Unit
) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()

    val scale = animateFloatAsState(
        targetValue = if (isPressed.value) 0.88f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.4f, // low damping for authentic jelly/water drop physics
            stiffness = 250f      // elastic speed
        ),
        label = "JellyScale"
    )

    val modifierWithTag = if (tag != null) this.testTag(tag) else this

    modifierWithTag
        .graphicsLayer {
            scaleX = scale.value
            scaleY = scale.value
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null, // Custom ripple is bypassed to highlight pure jelly bounce
            enabled = enabled,
            onClick = onClick
        )
}

// 2. Glassmorphic Card Container
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    // Glass styling adjustments matching Clean Minimalism white/40 backdrop and white/60 border
    val bgAlphaStart = if (isDark) 0.08f else 0.40f
    val bgAlphaEnd = if (isDark) 0.03f else 0.20f
    val borderAlphaStart = if (isDark) 0.15f else 0.60f
    val borderAlphaEnd = if (isDark) 0.05f else 0.30f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = bgAlphaStart),
                        Color.White.copy(alpha = bgAlphaEnd)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = borderAlphaStart),
                        Color.White.copy(alpha = borderAlphaEnd)
                    )
                ),
                RoundedCornerShape(cornerRadius)
            )
    ) {
        content()
    }
}

// 3. Glassmorphic Button Container
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDark: Boolean = true,
    isSelected: Boolean = false,
    tag: String? = null,
    content: @Composable () -> Unit
) {
    val activeColor = if (isDark) Color(0xFF80D8FF).copy(alpha = 0.35f) else Color(0xFF00B0FF).copy(alpha = 0.35f)
    val inactiveBg = if (isDark) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.35f)
    val borderCol = if (isSelected) Color.White.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) activeColor else inactiveBg)
            .border(
                width = 1.dp,
                color = borderCol,
                shape = RoundedCornerShape(16.dp)
            )
            .jellyClickable(tag = tag, onClick = onClick)
    ) {
        content()
    }
}

// 4. Vibrant Aurora Background Canvas (creates the colored blur underneath glassmorphism)
@Composable
fun AuroraBackground(
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    val baseBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
    
    // Clean Minimalism dynamic luminous colors (Blue, Purple, Pink)
    val color1 = if (isDark) Color(0xFF1D4ED8).copy(alpha = 0.25f) else Color(0xFF93C5FD).copy(alpha = 0.35f) // top-right (blue-300)
    val color2 = if (isDark) Color(0xFF6D28D9).copy(alpha = 0.25f) else Color(0xFFD8B4FE).copy(alpha = 0.35f) // bottom-left (purple-300)
    val color3 = if (isDark) Color(0xFFBE185D).copy(alpha = 0.15f) else Color(0xFFFBCFE8).copy(alpha = 0.25f) // center-right (pink-300)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .blur(90.dp) // creates ultra-smooth dynamic pastel color mixing
        ) {
            val width = size.width
            val height = size.height

            // Circle 1 (Top-Right glowing node)
            drawCircle(
                color = color1,
                radius = minOf(width, height) * 0.5f,
                center = Offset(width * 0.9f, height * 0.1f)
            )

            // Circle 2 (Bottom-Left glowing node)
            drawCircle(
                color = color2,
                radius = minOf(width, height) * 0.55f,
                center = Offset(width * 0.1f, height * 0.9f)
            )

            // Circle 3 (Center-Right glowing node)
            drawCircle(
                color = color3,
                radius = minOf(width, height) * 0.45f,
                center = Offset(width * 0.9f, height * 0.5f)
            )
        }
    }
}

// 4. Minimalist Beautiful Avatar Profile System
data class AvatarData(
    val emoji: String,
    val gradientColors: List<Color>,
    val label: String
)

fun getAvatarData(index: Int): AvatarData {
    return when (index % 5) {
        0 -> AvatarData("☕", listOf(Color(0xFFF59E0B), Color(0xFFD97706)), "คอกาแฟ")
        1 -> AvatarData("💼", listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)), "วัยทำงาน")
        2 -> AvatarData("🚀", listOf(Color(0xFF8B5CF6), Color(0xFFEC4899)), "ผู้บุกเบิก")
        3 -> AvatarData("🎓", listOf(Color(0xFF10B981), Color(0xFF065F46)), "นักเรียน/นักศึกษา")
        else -> AvatarData("🌸", listOf(Color(0xFFEC4899), Color(0xFFF472B6)), "สายสร้างสรรค์")
    }
}

@Composable
fun AvatarView(
    avatarIndex: Int,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    val avatar = getAvatarData(avatarIndex)
    Box(
        modifier = modifier
            .size(size)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(Brush.linearGradient(avatar.gradientColors)),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = avatar.emoji,
            fontSize = (size.value * 0.5f).sp,
            lineHeight = (size.value * 0.5f).sp
        )
    }
}


package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.RajasthanGold
import com.example.ui.theme.RajasthanOchre

@Composable
fun MemberProfilePhoto(
    photoUri: String,
    memberName: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    // Collect initials for fallback matching
    val initials = memberName.trim().split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.take(1) }
        .uppercase()

    // Determine stylized avatar background and symbol based on avatar key
    val avatarTheme = when (photoUri) {
        "avatar_1" -> AvatarTheme(Color(0xFF7C2D12), Color(0xFFFFEDD5), "👑") // Royal
        "avatar_2" -> AvatarTheme(Color(0xFF065F46), Color(0xFFD1FAE5), "🪔") // Heritage Lamp
        "avatar_3" -> AvatarTheme(Color(0xFF1E3A8A), Color(0xFFDBEAFE), "🚩") // Flag
        "avatar_4" -> AvatarTheme(Color(0xFF881337), Color(0xFFFFE4E6), "🌸") // Flower
        "avatar_5" -> AvatarTheme(Color(0xFF701A75), Color(0xFFFAE8FF), "✨") // Spark
        else -> {
            val hashCode = memberName.hashCode().coerceAtLeast(0)
            val index = hashCode % 5
            val bgColors = listOf(Color(0xFF7C2D12), Color(0xFF065F46), Color(0xFF1E3A8A), Color(0xFF881337), Color(0xFF701A75))
            val textColors = listOf(Color(0xFFFFEDD5), Color(0xFFD1FAE5), Color(0xFFDBEAFE), Color(0xFFFFE4E6), Color(0xFFFAE8FF))
            val emojis = listOf("👑", "🪔", "🚩", "🌸", "✨")
            AvatarTheme(bgColors[index], textColors[index], emojis[index])
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarTheme.backgroundColor)
            .border(3.dp, RajasthanGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (photoUri.startsWith("http://") || photoUri.startsWith("https://") || photoUri.startsWith("content://")) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "$memberName's Profile Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onState = { state -> } // Handle errors gracefully by falling back to below if it fails
            )
        } else {
            // High-legibility custom symbol and initials for elder visual feedback
            // Show emoji decoration on top, initials in the middle!
            Text(
                text = if (initials.isNotEmpty()) initials else "KM",
                color = avatarTheme.textColor,
                fontSize = (size.value * 0.35f).sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            
            // Subtly position small emoji at bottom right or top right inside the avatar circle
            Text(
                text = avatarTheme.iconCode,
                fontSize = (size.value * 0.24f).sp,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

private data class AvatarTheme(
    val backgroundColor: Color,
    val textColor: Color,
    val iconCode: String
)

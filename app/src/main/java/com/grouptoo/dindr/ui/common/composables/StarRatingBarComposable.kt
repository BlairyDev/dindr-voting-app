package com.grouptoo.dindr.ui.common.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StarRatingBar(
    rating: Double?,
) {
    val starSize = 20.dp
    val starSpacing = 0.5.dp
    val displayRating = rating ?: 0.0

    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= displayRating) Icons.Filled.Star else Icons.Default.Star,
                contentDescription = "Ratings system",
                tint = if (i <= displayRating) Color(0xFFFFCE00) else Color(0x4DFFFFFF),
                modifier = Modifier.size(starSize)
            )
            if (i < 5) Spacer(Modifier.width(starSpacing))
        }
    }
}

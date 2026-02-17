package com.apexfit.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateNavigationHeader(
    date: LocalDate,
    isToday: Boolean,
    streak: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onProfileTap: () -> Unit,
) {
    val dateText = if (isToday) {
        "TODAY"
    } else {
        date.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault())).uppercase()
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Left: Avatar + Streak
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BackgroundTertiary)
                    .clickable { onProfileTap() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = TextTertiary,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Icon(
                imageVector = Icons.Filled.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFFF9500),
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(Spacing.xs))
            Text(
                text = "$streak",
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
        }

        // Center: Date Navigation
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onPreviousDay, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous day",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }

            Text(
                text = dateText,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                modifier = Modifier
                    .background(
                        color = BackgroundTertiary,
                        shape = RoundedCornerShape(50),
                    )
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            )

            IconButton(
                onClick = onNextDay,
                enabled = !isToday,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next day",
                    tint = if (isToday) TextTertiary.copy(alpha = 0.3f) else TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Right: Watch icon
        Icon(
            imageVector = Icons.Filled.Watch,
            contentDescription = "Watch",
            tint = TextSecondary,
            modifier = Modifier.size(16.dp),
        )
    }
}

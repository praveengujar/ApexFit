package com.apexfit.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apexfit.core.designsystem.theme.BackgroundCard
import com.apexfit.core.designsystem.theme.BackgroundTertiary
import com.apexfit.core.designsystem.theme.CornerRadius
import com.apexfit.core.designsystem.theme.PrimaryBlue
import com.apexfit.core.designsystem.theme.Spacing
import com.apexfit.core.designsystem.theme.TextPrimary
import com.apexfit.core.designsystem.theme.TextSecondary
import com.apexfit.core.designsystem.theme.TextTertiary
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun JournalWeekCard(
    selectedDate: LocalDate,
    loggedDates: Set<LocalDate>,
    onJournalTap: () -> Unit,
) {
    val weekStart = selectedDate.minusDays(selectedDate.dayOfWeek.value.toLong() - 1)
    val weekDays = (0L..6L).map { weekStart.plusDays(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.medium))
            .background(BackgroundCard)
            .padding(Spacing.md),
    ) {
        Text(
            text = "JOURNAL",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            letterSpacing = 0.5.sp,
        )

        Spacer(Modifier.height(Spacing.md))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            weekDays.forEach { day ->
                val isLogged = loggedDates.contains(day)
                val isSelected = day == selectedDate
                val dayLabel = day.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    .first().uppercase()

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = dayLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextTertiary,
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> PrimaryBlue
                                    isLogged -> PrimaryBlue.copy(alpha = 0.3f)
                                    else -> BackgroundTertiary
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "${day.dayOfMonth}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected || isLogged) TextPrimary else TextTertiary,
                        )
                    }
                }
            }
        }
    }
}

package com.zyva.feature.myplan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zyva.core.designsystem.theme.BackgroundPrimary
import com.zyva.core.designsystem.theme.TextPrimary

@Composable
fun MyPlanScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "My Plan",
            style = MaterialTheme.typography.headlineLarge,
            color = TextPrimary,
        )
    }
}

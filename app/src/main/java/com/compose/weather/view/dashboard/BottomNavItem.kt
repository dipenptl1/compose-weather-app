package com.compose.weather.view.dashboard

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val header: String = title // In case header is not define use title as header
)

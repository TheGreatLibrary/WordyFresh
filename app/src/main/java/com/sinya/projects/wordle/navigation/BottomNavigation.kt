package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.domain.enums.TypeAppNavigation
import com.sinya.projects.wordle.domain.model.PopUpStrategy
import com.sinya.projects.wordle.ui.theme.WordyColor

@Composable
fun BottomNavigation(
    currentRoute: String?,
    navigateOn: (ScreenRoute, PopUpStrategy) -> Unit
) {
    val items = TypeAppNavigation.entries

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 550.dp)
                .padding(top = 1.dp)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(WordyColor.colors.backgroundCard)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route.route

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { navigateOn(item.route, PopUpStrategy.None) }
                            .background(Color.Transparent)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.iconId),
                            contentDescription = null,
                            tint = if (isSelected) WordyColor.colors.backPrimary else WordyColor.colors.textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

            }
        }
    }
}
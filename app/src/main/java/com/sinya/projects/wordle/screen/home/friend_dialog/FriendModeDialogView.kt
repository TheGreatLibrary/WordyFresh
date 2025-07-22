package com.sinya.projects.wordle.screen.home.friend_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.home.friend_dialog.components.CustomTab
import com.sinya.projects.wordle.screen.home.friend_dialog.components.GetTabContent
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun FriendModeDialogView(
    state: FriendModeUiState,
    onEvent: (FriendModeUiEvent) -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(visible = true) {
            Column(
                Modifier
                    .background(color = WordyColor.colors.background, shape = WordyShapes.large)
                    .padding(horizontal = 25.dp, vertical = 25.dp),
                verticalArrangement = Arrangement.spacedBy(17.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(R.string.friend_mode),
                    color = WordyColor.colors.textPrimary,
                    style = WordyTypography.titleLarge,
                    fontSize = 18.sp
                )
                TabRow(
                    selectedTabIndex = state.selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = WordyColor.colors.textPrimary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.selectedTab])
                                .height(2.dp),
                            color = WordyColor.colors.backPrimary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CustomTab(
                        selected = state.selectedTab == 0,
                        onClick = { onEvent(FriendModeUiEvent.OnTabClick(0)) },
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.get_shifr_word)
                    )
                    CustomTab(
                        selected = state.selectedTab == 1,
                        onClick = { onEvent(FriendModeUiEvent.OnTabClick(1)) },
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.put_shifr_word)
                    )
                }
                GetTabContent(
                    navigateTo = navigateTo,
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}
package com.sinya.projects.wordle.screen.onboarding

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.onboarding.components.DotsIndicator
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageAttempts
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageCellColors
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageFinish
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageRules
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageWelcome
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun OnboardingPager(
    changeLang: (String) -> Unit,
    isDark: StateFlow<Boolean>,
    isFirstPlay: Boolean,
    clearBackground: (Context) -> Unit,
    toggleTheme: (Boolean) -> Unit,
    onFinish: () -> Unit
) {
    val currentIsDark by isDark.collectAsState()
    val context = LocalContext.current

    val pages = listOf(
        if (!isFirstPlay) listOf(OnboardingPageUiState.Welcome) else emptyList(),
        listOf(
            OnboardingPageUiState.CellColors,
            OnboardingPageUiState.Attempts,
            OnboardingPageUiState.Rules,
            OnboardingPageUiState.Finish
        )
    ).flatten()
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,

        ) {
        AnimatedVisibility(pagerState.currentPage == pages.indices.first && !isFirstPlay) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageButton(
                    image = if (!currentIsDark) R.drawable.set_light else R.drawable.set_night,
                    modifierImage = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
                    onClick = {
                        clearBackground(context)
                        toggleTheme(!currentIsDark)
                    }
                )
                Text(
                    text = stringResource(R.string.skip),
                    modifier = Modifier.clickable { onFinish() },
                    style = WordyTypography.labelSmall,
                    color = WordyColor.colors.textPrimary
                )
            }
        }
        AnimatedVisibility(pagerState.currentPage != pages.indices.first || isFirstPlay) {
            DotsIndicator(
                totalDots = pages.size,
                selectedIndex = pagerState.currentPage,
            )
        }
        HorizontalPager(state = pagerState, userScrollEnabled = pagerState.currentPage != 0 || isFirstPlay) { page ->
            when (pages[page]) {
                is OnboardingPageUiState.Welcome -> PageWelcome(
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    changeLang = changeLang
                )
                is OnboardingPageUiState.CellColors -> PageCellColors()
                is OnboardingPageUiState.Attempts -> PageAttempts()
                is OnboardingPageUiState.Rules -> PageRules()
                is OnboardingPageUiState.Finish -> PageFinish(onFinish)
            }
        }
        Spacer(Modifier)
        Spacer(Modifier)
    }
}


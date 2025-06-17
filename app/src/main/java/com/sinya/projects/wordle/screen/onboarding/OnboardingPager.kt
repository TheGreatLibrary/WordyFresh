package com.sinya.projects.wordle.screen.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.onboarding.components.DotsIndicator
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageAttempts
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageCellColors
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageFinish
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageRules
import com.sinya.projects.wordle.screen.onboarding.subscreen.PageWelcome
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun OnboardingPager(
    changeLang: (String) -> Unit,
    isDark: StateFlow<Boolean>,
    toggleTheme: (Boolean) -> Unit,
    onFinish: () -> Unit
) {
    val currentIsDark by isDark.collectAsState()

    val pages = listOf(
        OnboardingPageUiState.Welcome,
        OnboardingPageUiState.CellColors,
        OnboardingPageUiState.Attempts,
        OnboardingPageUiState.Rules,
        OnboardingPageUiState.Finish
    )
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween,

        ) {
        AnimatedVisibility(pagerState.currentPage == pages.indices.first) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                ImageButton(
                    image = if (!currentIsDark) R.drawable.set_light else R.drawable.set_night,
                    modifierIcon = Modifier.size(32.dp),
                    colorFilter = ColorFilter.tint(WordleColor.colors.textPrimary),
                    onClick = { toggleTheme(!currentIsDark) }
                )
            }
        }
        AnimatedVisibility(pagerState.currentPage != pages.indices.first) {
            DotsIndicator(
                totalDots = pages.size,
                selectedIndex = pagerState.currentPage,
            )
        }
        HorizontalPager(state = pagerState, userScrollEnabled = pagerState.currentPage != 0) { page ->
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


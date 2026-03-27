package com.sinya.projects.wordle.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.OnboardingState
import com.sinya.projects.wordle.presentation.onboarding.components.DotsIndicator
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageAttempts
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageCellColors
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageFinish
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageMagic
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageRules
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageWelcome
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.LocalSettingsEngine
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.launch

@Composable
fun OnboardingPager(
    navigateTo: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val engine = LocalSettingsEngine.current
    val uiConfig by engine.uiState.collectAsStateWithLifecycle()

    val currentIsDark = uiConfig.dark
    val onboardingCompletedStatus = uiConfig.onboardingDone

    val pages = remember(onboardingCompletedStatus) {
        if (onboardingCompletedStatus != true) {
            OnboardingState.getFirstPlay()
        } else {
            OnboardingState.getNotFirstPlay()
        }
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedVisibility(
            visible = pagerState.currentPage == 0 && onboardingCompletedStatus != true
        ) {
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
                        engine.clearBackground()
                        engine.setDark(!currentIsDark)
                    }
                )
                TextButton(
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = WordyColor.colors.textPrimary,
                    ),
                    onClick = {
                        engine.setOnboardingState(true)
                        navigateTo()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.skip),
                        style = WordyTypography.labelSmall
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = pagerState.currentPage != 0 || onboardingCompletedStatus == true
        ) {
            DotsIndicator(
                totalDots = pages.size,
                selectedIndex = pagerState.currentPage
            )
        }

        HorizontalPager(
            state = pagerState,
            userScrollEnabled = pagerState.currentPage != 0 || onboardingCompletedStatus == true
        ) { page ->
            when (pages[page]) {
                OnboardingState.WELCOME -> PageWelcome(
                    onNext = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    changeLang = engine::setLang
                )

                OnboardingState.CELL_COLORS -> PageCellColors()

                OnboardingState.ATTEMPTS -> PageAttempts()

                OnboardingState.RULES -> PageRules()

                OnboardingState.MAGIC -> PageMagic()

                OnboardingState.FINISH -> PageFinish {
                    engine.setOnboardingState(true)
                    navigateTo()
                }
            }
        }
    }
}


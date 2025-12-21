package com.sinya.projects.wordle.presentation.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.DataStoreViewModel
import com.sinya.projects.wordle.domain.enums.OnboardingState
import com.sinya.projects.wordle.presentation.onboarding.components.DotsIndicator
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageAttempts
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageCellColors
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageFinish
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageRules
import com.sinya.projects.wordle.presentation.onboarding.subscreen.PageWelcome
import com.sinya.projects.wordle.ui.features.ImageButton
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import kotlinx.coroutines.launch

@Composable
fun OnboardingPager(
    navigateTo: () -> Unit,
    dataStoreViewModel: DataStoreViewModel = hiltViewModel()
) {
    val currentIsDark by dataStoreViewModel.darkMode.collectAsState()
    val onboardingCompletedStatus by dataStoreViewModel.onboardingCompleted.collectAsState()

    val pages = remember(onboardingCompletedStatus) {
        if (onboardingCompletedStatus != true) {
            OnboardingState.getFirstPlay()
        } else {
            OnboardingState.getNotFirstPlay()
        }
    }
    val pagerState = rememberPagerState(pageCount = { pages.size })

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 20.dp),
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
                        dataStoreViewModel.clearBackground()
                        dataStoreViewModel.setDarkMode(!currentIsDark)
                    }
                )
                TextButton(onClick = {
                    dataStoreViewModel.setOnboardingCompleted(true)
                    navigateTo()
                }) {
                    Text(
                        text = stringResource(R.string.skip),
                        style = WordyTypography.labelSmall,
                        color = WordyColor.colors.textPrimary
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
                    changeLang = dataStoreViewModel::setLanguage
                )

                OnboardingState.CELL_COLORS -> PageCellColors()

                OnboardingState.ATTEMPTS -> PageAttempts()

                OnboardingState.RULES -> PageRules()

                OnboardingState.FINISH -> PageFinish {
                    dataStoreViewModel.setOnboardingCompleted(true)
                    navigateTo()
                }
            }
        }
        Spacer(Modifier)
        Spacer(Modifier)
    }
}


package com.sinya.projects.wordle.screen.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.achievement.AchievementTrigger
import com.sinya.projects.wordle.data.achievement.objects.AchievementManager
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.home.components.HomePlaceholder
import com.sinya.projects.wordle.utils.sendSupportEmail
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient
) {
    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(
            supabase,
            AvatarRepository(supabase, context),
            context,
            WordyApplication.database
        )
    )

    val uiState = viewModel.state.value

    Box(
        Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.statusBars)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 50.dp),
    ) {
        Crossfade(targetState = uiState) { state ->
            when (state) {
                is HomeUiState.Loading -> HomePlaceholder()
                is HomeUiState.Success -> HomeScreenView(
                    state = state,
                    navigateTo = navigateTo,
                    sendEmail = {
                        sendSupportEmail(context)
                        viewModel.onEvent(HomeUiEvent.SendEmailSupport)
                    },
                    onEvent = state.onEvent
                )
                is HomeUiState.Error -> Text(
                    text = "Ошибка: ${state.message}",
                    modifier = Modifier.padding(top = 50.dp)
                )
            }
        }
    }
}


package com.sinya.projects.wordle.screen.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.data.local.repository.AvatarRepository
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.screen.profile.components.ProfilePlaceholder
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.theme.WordyColor
import io.github.jan.supabase.SupabaseClient

@Composable
fun ProfileScreen(
    navigateToBackStack: () -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    supabase: SupabaseClient
) {
    val context = LocalContext.current
    val db = WordyApplication.database

//    val db = remember { AppDatabase.getInstance(context) }

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(db, supabase, AvatarRepository(supabase, context))
    )

    val uiState = viewModel.state.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Header(
            title = stringResource(R.string.profile_screen),
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        Crossfade(targetState = uiState) { uiState ->
            when (uiState) {
                is ProfileUiState.Loading -> ProfilePlaceholder()
                is ProfileUiState.Success -> ProfileInAccount(
                    state = uiState,
                    navigateTo = navigateTo,
                )
                is ProfileUiState.NoAccount -> ProfileOutAccount(
                    navigateTo = navigateTo
                )
                is ProfileUiState.Error -> Text(
                    text = "Ошибка: ${uiState.message}",
                    modifier = Modifier.padding(top = 50.dp),
                    color = WordyColor.colors.textPrimary
                )
            }
        }
    }
}


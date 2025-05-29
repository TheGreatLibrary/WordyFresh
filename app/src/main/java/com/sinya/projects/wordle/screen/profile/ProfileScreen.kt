package com.sinya.projects.wordle.screen.profile

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.screen.profile.subscreens.ProfilePlaceholder
import com.sinya.projects.wordle.screen.profile.subscreens.ProfileWithAccount
import com.sinya.projects.wordle.screen.profile.subscreens.ProfileWithoutAccount
import com.sinya.projects.wordle.ui.components.ProfileUiState
import io.github.jan.supabase.SupabaseClient

@Composable
fun ProfileScreen(
    navController: NavController,
    supabase: SupabaseClient
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.provideFactory(db, supabase, AvatarRepository(supabase, context))
    )

    val uiState = viewModel.uiState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Header(stringResource(R.string.profile_screen), false, navController)

//        Crossfade(targetState = uiState) { uiState ->
            when (uiState) {
                is ProfileUiState.Loading -> ProfilePlaceholder()
                is ProfileUiState.Success -> ProfileWithAccount(
                    viewModel,
                    profile = uiState.profile,
                    avatarUri = uiState.avatarUri,
                    navController = navController
                )

                is ProfileUiState.NoAccount -> ProfileWithoutAccount(navController)
                is ProfileUiState.Error -> Text(
                    "Ошибка: ${uiState.message}",
                    modifier = Modifier.padding(top = 50.dp)
                )
            }
//        }
    }
}


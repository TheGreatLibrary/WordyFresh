package com.sinya.projects.wordle.screen.resetPassword

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.utils.findActivity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.parseSessionFromFragment

@Composable
fun ResetPasswordScreen(
    supabase: SupabaseClient,
    navigateToProfile: () -> Unit,
    navigateToBackStack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    LaunchedEffect(Unit) {
        val raw = activity?.intent?.dataString
        Log.d("reset", "$raw")
        val fragment = raw?.substringAfter("#")
        if (fragment != null) {
            try {
                val session = supabase.auth.parseSessionFromFragment(fragment)
                supabase.auth.importSession(session)
                Log.d("reset", "✅ Session imported: ${supabase.auth.currentUserOrNull()?.email}")
            } catch (e: Exception) {
                Log.e("reset", "❌ Failed to parse/import session: ${e.message}")
            }
        }
    }

    val db = WordyApplication.database
    val viewModel: ResetPasswordViewModel = viewModel(
        factory = ResetPasswordViewModel.provideFactory(db, supabase)
    )

    val state = viewModel.state.value

    Box(modifier = Modifier.fillMaxSize()) {
        when(state) {
            is ResetPasswordUiState.ResetForm -> ResetPasswordScreenView(
                navigateToBackStack = navigateToBackStack,
                state = state,
                onEvent = viewModel::onEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white, WordyShapes.extraLarge)
                    .padding(horizontal = 26.dp, vertical = 14.dp),
                onReset = navigateToProfile
            )
            is ResetPasswordUiState.LoadingReset -> {

            }
        }
    }
}
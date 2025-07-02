package com.sinya.projects.wordle.screen.home.friend_dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sinya.projects.wordle.WordyApplication
import com.sinya.projects.wordle.navigation.ScreenRoute

@Composable
fun FriendModeDialog(navigateTo: (ScreenRoute) -> Unit, onDismiss: () -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val viewModel: FriendViewModel = viewModel(
        factory = FriendViewModel.provideFactory(remember { WordyApplication.database }.wordDao())
    )

    LaunchedEffect(Unit) {
        viewModel.copyRequest
            .flowWithLifecycle(lifecycleOwner.lifecycle)
            .collect { cipher ->
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Шифр слова", cipher)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Шифр скопирован в буфер обмена!", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    FriendModeDialogView(
        state = viewModel.state.value,
        onEvent = viewModel::onEvent,
        navigateTo = navigateTo,
        onDismiss = onDismiss
    )
}
package com.sinya.projects.wordle.presentation.home.friendSheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.home.friendSheet.components.CustomTab
import com.sinya.projects.wordle.presentation.home.friendSheet.components.DecodeTab
import com.sinya.projects.wordle.presentation.home.friendSheet.components.EncodeTab
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.white

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendBottomSheet(
    navigateTo: (ScreenRoute) -> Unit,
    onDismissRequest: () -> Unit,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val modifier = Modifier
        .fillMaxWidth()
        .background(white, WordyShapes.large)
        .padding(horizontal = 12.dp, vertical = 10.dp)

    LaunchedEffect(Unit) {
        viewModel.copyRequest.collect { cipher ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Шифр слова", cipher)
            clipboard.setPrimaryClip(clip)

            snackbarHostState.showSnackbar(
                message = context.getString(R.string.cipher_copied),
                duration = SnackbarDuration.Short
            )
        }
    }
    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = WordyColor.colors.background
    ) {
        Column {
            Text(
                text = stringResource(R.string.friend_mode),
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp, start = 30.dp, end = 30.dp, bottom = 43.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
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
                        onClick = { viewModel.onEvent(FriendEvent.OnTabClick(0)) },
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.get_shifr_word)
                    )
                    CustomTab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.onEvent(FriendEvent.OnTabClick(1)) },
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.put_shifr_word)
                    )
                }

                when (state.selectedTab) {
                    0 -> EncodeTab(
                        hiddenPlace = state.hiddenPlace,
                        isError = state.isError,
                        onValueChange = { viewModel.onEvent(FriendEvent.OnHiddenPlaceChange(it)) },
                        onEncode = { viewModel.onEvent(FriendEvent.EncodeCipher) },
                        modifier = modifier
                    )
                    1 -> DecodeTab(
                        guessedPlace = state.guessedPlace,
                        isError = state.isError,
                        onValueChange = { viewModel.onEvent(FriendEvent.OnGuessedPlaceChange(it)) },
                        onDecode = { viewModel.onEvent(FriendEvent.DecodeCipher(navigateTo)) },
                        modifier = modifier
                    )
                }
            }
        }

    }
}


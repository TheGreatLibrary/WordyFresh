package com.sinya.projects.wordle.presentation.game.finishSheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.navigation.ScreenRoute
import com.sinya.projects.wordle.presentation.game.GameEvent
import com.sinya.projects.wordle.presentation.game.finishSheet.components.FinishBottomSection
import com.sinya.projects.wordle.presentation.game.finishSheet.components.FinishHeaderSection
import com.sinya.projects.wordle.ui.features.CustomModalSheet
import com.sinya.projects.wordle.utils.openUrl
import com.sinya.projects.wordle.utils.shareResultOfGame

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishBottomSheet(
    sheetState: SheetState,
    state: FinishStatisticGame?,
    onEvent: (GameEvent) -> Unit,
    navigateTo: (ScreenRoute) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val onShare: (String, String, String) -> Unit = remember {
        { word, description, colorsBox ->
           context.shareResultOfGame(word, description, colorsBox)
        }
    }
    val navigateToDictionary: () -> Unit = remember {
        {
            context.openUrl(LegalLinks.formatAcademicUrl(state?.hiddenWord ?: ""))
        }
    }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    var sheetHeightPx by remember { mutableFloatStateOf(0f) }
    var buttonsHeightDp by remember { mutableStateOf(0.dp) }

    CustomModalSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .onGloballyPositioned { coordinates ->
                    sheetHeightPx = coordinates.size.height.toFloat()
                }
        ) {
            if (state != null) {
                FinishHeaderSection(
                    state = state,
                    navigateTo = navigateTo,
                    buttonsHeightDp = buttonsHeightDp,
                    navigateToDictionary = navigateToDictionary
                )

                FinishBottomSection(
                    modifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            buttonsHeightDp = with(density) { coordinates.size.height.toDp() }
                        }
                        .graphicsLayer {
                            val offset = try {
                                sheetState.requireOffset()
                            } catch (e: Exception) {
                                0f
                            }

                            val bottomOfSheet = offset + sheetHeightPx
                            val overflow = bottomOfSheet - screenHeightPx

                            translationY = if (overflow > 0) -overflow else 0f
                        },
                    state = state,
                    onEvent = onEvent,
                    onShare = onShare
                )
            }
        }
    }
}
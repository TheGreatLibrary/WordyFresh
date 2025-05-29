package com.sinya.projects.wordle.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.data.repository.AvatarRepository
import com.sinya.projects.wordle.dialog.BottomSheetDialog
import com.sinya.projects.wordle.dialog.friend_dialog.FriendModeDialog
import com.sinya.projects.wordle.domain.model.data.SavedGame
import com.sinya.projects.wordle.screen.profile.subscreens.Avatar
import com.sinya.projects.wordle.ui.components.ImageButton
import com.sinya.projects.wordle.ui.components.RoundedButton
import com.sinya.projects.wordle.ui.theme.Montserrat
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.WordleShapes
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.ui.theme.yellow
import io.github.jan.supabase.SupabaseClient
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
@Composable
fun HomeScreen(
    navController: NavHostController,
    supabase: SupabaseClient
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val modeGame = remember { mutableIntStateOf(0) }
    if (showBottomSheet.value) {
        BottomSheetDialog(
            navController,
            onDismissRequest = { showBottomSheet.value = false },
            modeGame.intValue
        )
    }
    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.provideFactory(supabase, AvatarRepository(supabase, context)))

    LaunchedEffect(Unit) {
        viewModel.loadAvatar()
    }


    var hasSavedGame by remember { mutableStateOf<SavedGame?>(null) }

    LaunchedEffect(Unit) {
        hasSavedGame = AppDataStore.loadGame(context)
    }

    Column(
        Modifier
            .fillMaxSize()
            .consumeWindowInsets(WindowInsets.statusBars)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            MainHeader(navController, viewModel)
            MainContainers(navController, showBottomSheet, modeGame)
        }
        Box(contentAlignment = Alignment.BottomCenter) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (hasSavedGame != null) {
                    RoundedButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .shadow(8.dp, spotColor = gray800),
                        ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkIII),
                        { navController.navigate("game/-1/${hasSavedGame?.length}/${hasSavedGame?.lang}/${hasSavedGame?.targetWord}") })
                    {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(R.string.continue_text),
                                fontSize = 16.sp,
                                color = WordleColor.colors.textColorMkII,
                                style = TextStyle(
                                    lineHeight = 16.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Text(
                                "${
                                    hasSavedGame?.length?.let {
                                        context.resources.getQuantityString(
                                            R.plurals.letters_count,
                                            it, hasSavedGame?.length
                                        )
                                    }
                                } - ${
                                    String.format(
                                        "%02d:%02d",
                                        TimeUnit.SECONDS.toMinutes(hasSavedGame?.totalSeconds ?: 0L)
                                            .toInt(),
                                        (hasSavedGame?.totalSeconds ?: 0L) % 60
                                    )
                                }",
                                fontSize = 12.sp,
                                color = WordleColor.colors.textColorMkII,
                                style = TextStyle(
                                    lineHeight = 12.sp,
                                    fontFamily = Montserrat,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
                RoundedButton(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .shadow(8.dp, spotColor = gray800),
                    ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkI),
                    {
                        showBottomSheet.value = true
                        modeGame.intValue = 0
                    }) {
                    Text(
                        stringResource(R.string.new_game),
                        fontSize = 16.sp,
                        color = WordleColor.colors.onTextColor,
                        style = WordleTypography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MainHeader(navController: NavController, viewModel: HomeViewModel) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
         Avatar(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .border(2.dp, WordleColor.colors.primary, CircleShape),
            viewModel.avatar.value,
        ) { navController.navigate("profile") }
//        ImageButton(
//            R.drawable.avatar,
//            modifier = Modifier
//                .size(42.dp)
//                .border(2.dp, color = WordleColor.colors.primary, WordleShapes.extraLarge)
//        ) { navController.navigate("profile") }
        ImageButton(R.drawable.home_mail, modifier = Modifier.size(32.dp)) {
            sendSupportEmail(
                context
            )
        }
    }
}

fun sendSupportEmail(context: Context) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("programming.creature@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Обращение в поддержку")
        putExtra(Intent.EXTRA_TEXT, "Опишите вашу проблему здесь...")
    }
    context.startActivity(Intent.createChooser(emailIntent, "Выберите приложение для отправки"))
}

@Composable
fun MainContainers(
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    modeGame: MutableState<Int>
) {
    var showFriendDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .padding(top = 29.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MainContainer(
            R.drawable.home_mode_friend,
            green800,
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            stringResource(R.string.friend_mode)
        ) { showFriendDialog = true }
        MainContainer(
            R.drawable.home_mode_hard,
            red,
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            stringResource(R.string.hard_mode)
        ) {
            showBottomSheet.value = true
            modeGame.value = 1
        }
        MainContainer(
            R.drawable.home_mode_random,
            yellow,
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            stringResource(R.string.random_mode)
        ) { navController.navigate("game/3/null/null/") }
    }

    if (showFriendDialog) {
        FriendModeDialog(
            navController,
            showFriendDialog,
            onDismiss = { showFriendDialog = false }
        )
    }
}

@Composable
fun MainContainer(
    image: Int,
    color: Color,
    modifier: Modifier,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .shadow(8.dp, spotColor = gray800)
            .then(modifier),
        shape = WordleShapes.large,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 10.dp, horizontal = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = WordleColor.colors.textTitleColor,
                style = WordleTypography.titleLarge)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(image),
                contentDescription = "iconCont",
                modifier = Modifier
                    .padding(top = 7.dp, bottom = 10.dp)
                    .clip(CircleShape)
                    .background(color = white)
                    .border(1.dp, gray600, CircleShape)
                    .size(41.dp)
                    .scale(0.75f),
                colorFilter = ColorFilter.tint(WordleColor.colors.foregroundIcon)
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(22.dp)
                    .padding(horizontal = 17.dp),
                shape = WordleShapes.large,
                colors = ButtonDefaults.buttonColors(containerColor = WordleColor.colors.backgroundBtnMkI),
                onClick = onClick,
                contentPadding = PaddingValues(vertical = 0.dp)
            ) {
                Text(
                    stringResource(R.string.play),
                    fontSize = 12.sp,
                    color = WordleColor.colors.textColorMkI,
                    style = WordleTypography.bodyMedium
                )
            }
        }
    }
}
}

@Composable
fun MainButton(colors: ButtonColors, onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .shadow(8.dp, spotColor = gray800),
        shape = CircleShape,
        colors = colors,
        onClick = onClick,
        contentPadding = PaddingValues(vertical = 5.dp)
    ) {
        content()
    }
}
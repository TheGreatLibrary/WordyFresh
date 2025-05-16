package com.sinya.projects.wordle.screen.dictionary

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.database.AppDatabase
import com.sinya.projects.wordle.domain.model.data.DictionaryItem
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.components.CustomTextField
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.gray100
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.gray800
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
//    val dictionaryDao = db.offlineDictionaryDao()

    val viewModel: DictionaryViewModel = viewModel(
        factory = DictionaryViewModel.provideFactory(db, context)
    )
    val pullToRefreshState = rememberPullToRefreshState()

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            delay(1000)
            pullToRefreshState.endRefresh()
        }
    }

    Box(Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
        ) {
            Header("Словарь", false, navController)
            SearchContainer(viewModel)
            DictionaryList(viewModel.getFilteredList(), viewModel)
        }
        PullToRefreshContainer(
            modifier = Modifier.align(Alignment.TopCenter),
            state = pullToRefreshState,
        )
    }

}

@Composable
fun SearchContainer(viewModel: DictionaryViewModel) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(top = 18.dp)
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(100)),
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "iconCont",
                )
                CustomTextField(
                    viewModel.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    "Введите слово",
                    Modifier.fillMaxWidth(0.88f)
                )
            }
            VoiceInputButton { spokenText -> viewModel.updateSearchQuery(spokenText) }
        }
    }
}

@Composable
fun VoiceInputButton(onVoiceInput: (String) -> Unit) {
    val context = LocalContext.current
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("VoiceInput", "Готов к записи")
                }

                override fun onBeginningOfSpeech() {
                    Log.d("VoiceInput", "Начало записи")
                }

                override fun onRmsChanged(rmsdB: Float) {} // Громкость речи
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    Log.d("VoiceInput", "Конец записи")
                }

                override fun onError(error: Int) {
                    Log.e("VoiceInput", "Ошибка распознавания: $error")
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        Log.d("VoiceInput", "Распознано: ${matches[0]}")
                        onVoiceInput(matches[0])
                    }
                }
            })
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening(speechRecognizer)
        } else {
            Log.e("VoiceInput", "Нет разрешения на запись звука")
        }
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = white
        ),
        onClick = {
            if (ContextCompat.checkSelfPermission(
                    context,
                    RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startListening(speechRecognizer)
            } else {
                permissionLauncher.launch(RECORD_AUDIO)
            }
        },
        contentPadding = PaddingValues(0.dp)
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.ic_micro),
            contentDescription = "Voice Input"
        )
    }
}

private fun startListening(speechRecognizer: SpeechRecognizer) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }
    speechRecognizer.startListening(intent)
    Log.d("VoiceInput", "Начинаем слушать...")
}

@Composable
fun DictionaryList(items: List<DictionaryItem>, viewModel: DictionaryViewModel) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(top = 21.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        items(items.size) { item ->
            DictionaryCard(items[item].word, items[item].description, viewModel)
        }
    }
}

@Composable
fun DictionaryCard(title: String, description: String, viewModel: DictionaryViewModel) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) -90f else 90f, label = "")
    val coroutineScope = rememberCoroutineScope()

    Card(
        Modifier
            .shadow(elevation = 5.dp, spotColor = gray800, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(15.dp))
            .clickable {
                expanded = !expanded
            },
        colors = CardDefaults.cardColors(containerColor = white)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(color = white)
                .padding(horizontal = 12.dp, vertical = 13.dp),
        ) {
            Row(
                Modifier.fillMaxWidth(0.98f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 14.sp, color = gray600, style = WordleTypography.bodyLarge)
                Image(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = "ar",
                    colorFilter = ColorFilter.tint(green800),
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    HorizontalDivider(
                        color = red,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(0.13f)
                    )
                    Text(
                        description.ifEmpty { "В словаре нет определения, нажмите на 1 кнопку для поиска в Интернете" },
                        fontSize = 14.sp,
                        color = gray600,
                        style = WordleTypography.bodyMedium
                    )
                    Row(
                        Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_serch_dict),
                            contentDescription = "d",
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = gray100)
                                .size(28.dp)
                                .scale(0.8f)
                                .clickable {
                                    val url = "https://academic.ru/searchall.php?SWord=$title"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                            colorFilter = ColorFilter.tint(gray600)
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_share),
                            contentDescription = "d",
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = gray100)
                                .size(28.dp)
                                .scale(0.8f)
                                .clickable {
                                    val textToShare =
                                        "Что такое $title? " + if (description.isEmpty()) "" else {
                                            "$description. "
                                        } + "Я узнал об этом в Wordy Fresh, " +
                                                "которую можно найти тут: " +
                                                "https://www.rustore.ru/catalog/app/com.sinya.example.wordle"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, textToShare)
                                    }
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Поделиться через"
                                        )
                                    )
                                },
                            colorFilter = ColorFilter.tint(gray600)
                        )
                        Image(
                            painter = painterResource(R.drawable.ic_reload),
                            contentDescription = "d",
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = gray100)
                                .size(28.dp)
                                .scale(0.8f)
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.reloadDescription(title)
                                    }
                                },
                            colorFilter = ColorFilter.tint(gray600)
                        )
                    }
                }
            }
        }
    }
}
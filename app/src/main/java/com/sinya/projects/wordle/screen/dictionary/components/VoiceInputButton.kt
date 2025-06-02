package com.sinya.projects.wordle.screen.dictionary.components

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.white
import com.sinya.projects.wordle.utils.startListening

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
            painter = painterResource(R.drawable.dict_micro),
            contentDescription = "Voice Input"
        )
    }
}

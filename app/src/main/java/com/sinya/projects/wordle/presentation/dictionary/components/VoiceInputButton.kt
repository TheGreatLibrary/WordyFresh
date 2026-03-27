package com.sinya.projects.wordle.presentation.dictionary.components

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.domain.enums.VibrationType
import com.sinya.projects.wordle.utils.startListening

@Composable
fun VoiceInputButton(
    onVoiceInput: (String) -> Unit,
    onVibrate: (VibrationType) -> Unit
) {
    val context = LocalContext.current
    var isListening by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val micScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micScale"
    )

    val micAlpha by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micAlpha"
    )

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    DisposableEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                onVibrate(VibrationType.VOICE_START)
            }

            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
                onVibrate(VibrationType.VOICE_END)
            }

            override fun onError(error: Int) {
                Log.e("VoiceInput", "Ошибка: $error")
                isListening = false
                onVibrate(VibrationType.WRONG_LETTER)
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.firstOrNull()?.let { text ->
                    onVoiceInput(text)
                }
                isListening = false
                onVibrate(VibrationType.VOICE_END)
            }
        })

        onDispose {
            speechRecognizer.destroy()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startListening(speechRecognizer)
        } else {
            Log.e("VoiceInput", "Нет разрешения")
        }
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        if (isListening) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .aspectRatio(1f)
                    .scale(micScale)
                    .alpha(micAlpha * 0.3f)
                    .background(
                        WordyColor.colors.primary,
                        shape = WordyShapes.extraLarge
                    )
            )
        }
        IconButton(
            onClick = {
                when {
                    isListening -> {
                        speechRecognizer.stopListening()
                        onVibrate(VibrationType.VOICE_END)
                    }
                    ContextCompat.checkSelfPermission(context, RECORD_AUDIO) ==
                            PackageManager.PERMISSION_GRANTED -> {
                        startListening(speechRecognizer)
                        onVibrate(VibrationType.WRONG_LETTER)
                    }
                    else -> permissionLauncher.launch(RECORD_AUDIO)
                }
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.dict_micro),
                contentDescription = "voice",
                tint = if (isListening) WordyColor.colors.primary else WordyColor.colors.backgroundPassiveBtn,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

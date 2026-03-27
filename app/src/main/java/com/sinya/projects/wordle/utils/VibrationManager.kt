package com.sinya.projects.wordle.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.VibrationEffect.EFFECT_HEAVY_CLICK
import android.os.VibrationEffect.EFFECT_TICK
import android.os.Vibrator
import android.os.VibratorManager
import com.sinya.projects.wordle.data.local.datastore.SettingsEngine
import com.sinya.projects.wordle.domain.enums.VibrationType
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class VibrationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsEngine: SettingsEngine
) {
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun vibrate(type: VibrationType) {
        if (!settingsEngine.uiState.value.vibrationStatus) return
        if (!vibrator.hasVibrator()) return

        val effect = when (type) {
            VibrationType.WRONG_LETTER -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(EFFECT_TICK)
            } else {
                VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
            }

            VibrationType.WRONG_WORD   -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(EFFECT_HEAVY_CLICK)
            } else {
                VibrationEffect.createWaveform(longArrayOf(0, 80, 60, 80), -1)
            }

            VibrationType.WIN          -> VibrationEffect.createWaveform(
                longArrayOf(0, 80, 40, 80, 40, 120),
                intArrayOf(0, 150, 0, 180, 0, 255), -1
            )

            VibrationType.LOSE         -> VibrationEffect.createOneShot(400, 180)

            VibrationType.HINT_USED    -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                VibrationEffect.createPredefined(EFFECT_CLICK)
            } else {
                VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE)
            }

            VibrationType.VOICE_START  -> VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
            VibrationType.VOICE_END    -> VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE)

            VibrationType.KEY_LETTER -> VibrationEffect.createOneShot(10, 15)

            VibrationType.KEY_DELETE -> VibrationEffect.createOneShot(10, 30)

            VibrationType.KEY_ENTER  -> VibrationEffect.createOneShot(20, 60)
        }

        vibrator.vibrate(effect)
    }
}


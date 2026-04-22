package com.sinya.projects.wordle.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.sendSupportEmail() {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf("programming.creature@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Обращение в поддержку")
        putExtra(Intent.EXTRA_TEXT, "Опишите вашу проблему здесь...")
    }
    startActivity(Intent.createChooser(emailIntent, "Выберите приложение для отправки"))
}

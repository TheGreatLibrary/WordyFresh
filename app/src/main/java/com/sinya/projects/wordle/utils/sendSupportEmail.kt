package com.sinya.projects.wordle.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun sendSupportEmail(context: Context) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf("programming.creature@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT, "Обращение в поддержку")
        putExtra(Intent.EXTRA_TEXT, "Опишите вашу проблему здесь...")
    }
    context.startActivity(Intent.createChooser(emailIntent, "Выберите приложение для отправки"))
}

package com.sinya.projects.wordle.utils

import android.content.Context
import android.content.Intent
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks

fun Context.shareDescription(
    word: String,
    description: String
) {
    val text = getString(
        R.string.share_text,
        word,
        description.ifEmpty { "" },
        LegalLinks.WORDY_APP_URL
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(
        Intent.createChooser(intent, getString(R.string.shared_to))
    )
}
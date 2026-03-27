package com.sinya.projects.wordle.ui.features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

@Composable
fun getLocalizedString(key: String): String {
    val context = LocalContext.current
    val resId = remember(key) {
        context.resources.getIdentifier(key, "string", context.packageName)
    }
    return if (resId != 0) {
        stringResource(id = resId)
    } else {
        key
    }
}
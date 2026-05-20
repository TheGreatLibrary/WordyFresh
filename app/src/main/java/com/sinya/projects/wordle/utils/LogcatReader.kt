package com.sinya.projects.wordle.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object LogcatReader {
    suspend fun getAppLogs(context: Context): String = withContext(Dispatchers.IO) {
        val logBuilder = StringBuilder()
        try {
            val myPid = android.os.Process.myPid().toString()
            val packageName = context.packageName

            val process = Runtime.getRuntime().exec("logcat -d -v time *:D")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                val currentLine = line ?: continue

                val isMyProcess = currentLine.contains(myPid) || currentLine.contains(packageName)
                val isStackTrace = currentLine.contains("at ") || currentLine.contains("Caused by:")

                if (isMyProcess || isStackTrace) {
                    logBuilder.append(currentLine).append("\n")
                }
            }
        } catch (e: Exception) {
            logBuilder.append("Ошибка чтения Logcat: ${e.message}")
        }

        if (logBuilder.isEmpty()) "Логи пусты." else logBuilder.toString()
    }
}
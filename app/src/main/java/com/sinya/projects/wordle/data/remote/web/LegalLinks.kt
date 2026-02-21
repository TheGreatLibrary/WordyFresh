package com.sinya.projects.wordle.data.remote.web

object LegalLinks {
    const val PRIVACY_POLICY_URL = "https://picnic-bk.ru/alex/wordy_fresh/privacy_policy.html"
    const val TERMS_OF_USE_URL = "https://picnic-bk.ru/alex/wordy_fresh/terms_of_use.html"
    const val WORDY_APP_URL = "https://www.rustore.ru/catalog/app/com.sinya.projects.wordle"
    private const val ACADEMIC_DICTIONARY_TEMPLATE = "https://academic.ru/searchall.php?SWord=%s"
    private const val WIKI_URL_TEMPLATE = "https://%s.wikipedia.org/api/rest_v1/page/summary/%s"
    private const val AVATAR_FILE_NAME = "avatar_%s.webp"

    const val RESET_PASSWORD = "wordy-fresh://reset-password"
    const val RESET_EMAIL = "wordy-fresh://reset-email"
    const val EMAIL_CONFIRMED = "wordy-fresh://email-confirmed"

    fun formatAcademicUrl(word: String) = ACADEMIC_DICTIONARY_TEMPLATE.format(word)
    fun formatWikiUrl(lang: String, encodedWord: String) = WIKI_URL_TEMPLATE.format(lang, encodedWord)
    fun getAvatarFileName(userId: String) = AVATAR_FILE_NAME.format(userId)
}
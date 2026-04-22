package com.sinya.projects.wordle.data.remote.web

object LegalLinks {
    const val PRIVACY_POLICY_URL = "https://thegreatlibrary.github.io/wordy-fresh-invite/privacy_policy"
    const val TERMS_OF_USE_URL = "https://thegreatlibrary.github.io/wordy-fresh-invite/terms_of_use"
    private const val INVITE_URL = "https://thegreatlibrary.github.io/wordy-fresh-invite/?word=%s"
    const val WORDY_APP_URL = "https://www.rustore.ru/catalog/app/com.sinya.projects.wordle"
    private const val ACADEMIC_DICTIONARY_TEMPLATE = "https://academic.ru/searchall.php?SWord=%s"
    private const val WIKI_URL_TEMPLATE = "https://%s.wikipedia.org/api/rest_v1/page/summary/%s"
    private const val WIKTIONARY_URL_TEMPLATE = "https://%s.wiktionary.org/w/api.php?action=query&titles=%s&prop=extracts&explaintext=true&format=json"
    private const val AVATAR_FILE_NAME = "avatar_%s.webp"

    const val RESET_PASSWORD = "wordy-fresh://reset-password"
    const val INVITE_GAME = "wordy-fresh://invite?word={word}"
    const val RESET_EMAIL = "wordy-fresh://reset-email"
    const val EMAIL_CONFIRMED = "wordy-fresh://email-confirmed"

    fun formatInviteUrl(word: String) =  INVITE_URL.format(word)
    fun formatAcademicUrl(word: String) = ACADEMIC_DICTIONARY_TEMPLATE.format(word)
    fun formatWikiUrl(lang: String, encodedWord: String) = WIKI_URL_TEMPLATE.format(lang, encodedWord)
    fun formatWiktionaryUrl(lang: String, encodedWord: String) = WIKTIONARY_URL_TEMPLATE.format(lang, encodedWord)
    fun getAvatarFileName(userId: String) = AVATAR_FILE_NAME.format(userId)
}
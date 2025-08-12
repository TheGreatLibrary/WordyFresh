package com.sinya.projects.wordle.screen.register.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.screen.register.RegisterUiEvent
import com.sinya.projects.wordle.screen.register.RegisterUiState
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.openUrl

@Composable
fun AcceptPolicy(
    state: RegisterUiState.RegisterForm,
    onEvent: (RegisterUiEvent) -> Unit,

) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            if (state.checkboxStatus) painterResource(R.drawable.checkbox_on) else painterResource(
                R.drawable.checkbox_off),
            null,
            modifier = Modifier
                .size(28.dp)
                .clickable {
                    onEvent(RegisterUiEvent.CheckboxStatusChanged(!state.checkboxStatus))
                },
            colorFilter = ColorFilter.tint(color = if (!state.isCheckboxError || state.checkboxStatus) WordyColor.colors.textPrimary
             else WordyColor.colors.secondary, blendMode = BlendMode.SrcIn)
        )
        TermsText({
            openUrl(context, LegalLinks.TERMS_OF_USE_URL)
        }, {
            openUrl(context, LegalLinks.PRIVACY_POLICY_URL)
        })
    }
}

@Composable
private fun TermsText(onTermsClick: () -> Unit, onPrivacyClick: () -> Unit) {
    val termsText = stringResource(id = R.string.terms_of_use_clickable)
    val privacyText = stringResource(id = R.string.privacy_policy_clickable)
    val baseText = stringResource(id = R.string.terms_and_privacy, termsText, privacyText)

    val annotatedText = buildAnnotatedString {
        val termsStart = baseText.indexOf(termsText)
        val privacyStart = baseText.indexOf(privacyText)

        append(baseText)

        addStyle(
            style = WordyTypography.labelSmall.toSpanStyle(),
            start = termsStart,
            end = termsStart + termsText.length
        )
        addStringAnnotation("TERMS", "terms", termsStart, termsStart + termsText.length)

        addStyle(
            style = WordyTypography.labelSmall.toSpanStyle(),
            start = privacyStart,
            end = privacyStart + privacyText.length
        )
        addStringAnnotation("PRIVACY", "privacy", privacyStart, privacyStart + privacyText.length)
    }

    ClickableText(
        text = annotatedText,
        style = TextStyle(
            color = WordyColor.colors.textPrimary,
            fontSize = 14.sp,
            fontFamily = WordyTypography.bodyMedium.fontFamily,
            fontWeight = FontWeight.W500
        )
    ) { offset ->
        annotatedText.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
            .firstOrNull()?.let { onTermsClick() }

        annotatedText.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
            .firstOrNull()?.let { onPrivacyClick() }
    }

}


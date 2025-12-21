package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography


@Composable
fun AcceptPolicyCheckbox(
    isChecked: Boolean,
    isError: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            if (isChecked) painterResource(R.drawable.checkbox_on) else painterResource(
                R.drawable.checkbox_off
            ),
            null,
            modifier = Modifier
                .size(28.dp)
                .clickable { onCheckedChange(!isChecked) },
            colorFilter = ColorFilter.tint(
                color = if (!isError || isChecked) WordyColor.colors.textPrimary
                else WordyColor.colors.secondary, blendMode = BlendMode.SrcIn
            )
        )

        TermsAndPrivacyText()
    }
}

@Composable
private fun TermsAndPrivacyText() {
    val termsText = stringResource(R.string.terms_of_use_clickable)
    val privacyText = stringResource(R.string.privacy_policy_clickable)
    val baseText = stringResource(R.string.terms_and_privacy, termsText, privacyText)

    val annotatedString = buildAnnotatedString {
        val termsStart = baseText.indexOf(termsText)
        val privacyStart = baseText.indexOf(privacyText)

        append(baseText)
        if (termsStart >= 0) {
            addLink(
                url = LinkAnnotation.Url(
                    url = LegalLinks.TERMS_OF_USE_URL,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = WordyColor.colors.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                ),
                start = termsStart,
                end = termsStart + termsText.length
            )
        }

        if (privacyStart >= 0) {
            addLink(
                url = LinkAnnotation.Url(
                    url = LegalLinks.PRIVACY_POLICY_URL,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            color = WordyColor.colors.primary,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                ),
                start = privacyStart,
                end = privacyStart + privacyText.length
            )
        }
    }

    Text(
        text = annotatedString,
        style = WordyTypography.bodyMedium,
        fontSize = 14.sp,
        color = WordyColor.colors.textPrimary,
        modifier = Modifier.fillMaxWidth(),
        onTextLayout = {}
    )
}

package com.sinya.projects.wordle.presentation.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.presentation.settings.components.AppVersionInfo
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.features.ScreenColumn
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.openUrl

@Composable
fun AboutScreen(
    navigateToBackStack: () -> Unit
) {
    val context = LocalContext.current

    ScreenColumn(
        title = stringResource(R.string.about_app_screen),
        navigateBack = navigateToBackStack
    ) {
        CustomCard(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "logo"
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontSize = 14.sp,
                        style = WordyTypography.bodyLarge,
                        color = WordyColor.colors.textCardPrimary
                    )
                    AppVersionInfo()
                    Text(
                        text = stringResource(R.string.license),
                        fontSize = 12.sp,
                        style = WordyTypography.bodyMedium,
                        color = WordyColor.colors.textCardSecondary
                    )
                }
            }
        }
        CardColumn {
            RowLink(
                title = stringResource(R.string.policy_privacy),
                mode = "",
                icon = R.drawable.prof_privacy,
                icon2 = R.drawable.arrow,
                navigateTo = {
                    context.openUrl(LegalLinks.PRIVACY_POLICY_URL)
                }
            )
            RowLink(
                title = stringResource(R.string.terms_of_use),
                mode = "",
                icon = R.drawable.prof_terms,
                icon2 = R.drawable.arrow,
                navigateTo = {
                    context.openUrl(LegalLinks.TERMS_OF_USE_URL)
                }
            )
        }
    }
}
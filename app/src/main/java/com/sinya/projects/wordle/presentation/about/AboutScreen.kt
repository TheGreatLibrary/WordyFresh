package com.sinya.projects.wordle.presentation.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.remote.web.LegalLinks
import com.sinya.projects.wordle.presentation.settings.components.AppVersionInfo
import com.sinya.projects.wordle.ui.features.CardColumn
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.features.RowLink
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.utils.openUrl

@Composable
fun AboutScreen(
    navigateToBackStack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Header(
            title = stringResource(R.string.about_app_screen),
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        Spacer(Modifier.height(0.dp))
        CustomCard(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
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
                    openUrl(context, LegalLinks.PRIVACY_POLICY_URL)
                }
            )
            RowLink(
                title = stringResource(R.string.terms_of_use),
                mode = "",
                icon = R.drawable.prof_terms,
                icon2 = R.drawable.arrow,
                navigateTo = {
                   openUrl(context, LegalLinks.TERMS_OF_USE_URL)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AboutPreview() {
    AboutScreen(
        navigateToBackStack = {}
    )
}
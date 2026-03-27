package com.sinya.projects.wordle.presentation.onboarding.subscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.presentation.game.components.WordCell
import com.sinya.projects.wordle.presentation.onboarding.OnboardingData
import com.sinya.projects.wordle.presentation.onboarding.components.OnboardingPageTemplate
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PageMagic() {
    val cells = remember { OnboardingData.getMagicExamples() }
    val focusedCell = remember { 24 }

    OnboardingPageTemplate {
        Column(
            verticalArrangement = Arrangement.spacedBy(9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.over_your_step),
                style = WordyTypography.titleLarge,
                fontSize = 24.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.use_2_magic_of_game),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(height = 40.dp, width = 60.dp)
                    .clip(WordyShapes.small)
                    .background(WordyColor.colors.textLinkColor)
                    .clickable { },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.game_magic),
                    contentDescription = null,
                    modifier = Modifier.height(21.dp),
                    colorFilter = ColorFilter.tint(WordyColor.colors.textPrimary),
                )
                Box {
                    Text(
                        text = "2/3",
                        fontSize = 11.sp,
                        color = WordyColor.colors.textPrimary,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
            Text(
                text = stringResource(R.string.simple_tap_btn),
                style = WordyTypography.bodyMedium,
                fontSize = 16.sp,
                color = WordyColor.colors.textPrimary,
                textAlign = TextAlign.Center
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                space = 4.dp,
                alignment = Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = 5
        ) {
            cells.forEachIndexed { rowIndex, row ->
                WordCell(
                    cell = row,
                    isFocused = focusedCell == rowIndex,
                    onClick = { },
                    modifier = Modifier.size(45.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.use_magic_with_brain),
            style = WordyTypography.bodyMedium,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = WordyColor.colors.textPrimary
        )
    }
}
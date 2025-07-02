package com.sinya.projects.wordle.screen.dictionary.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.screen.dictionary.DictionaryUiEvent
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.red

@Composable
fun DictionaryCard(
    context: Context,
    title: String,
    description: String,
    onEvent: (DictionaryUiEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) -90f else 90f, label = "")

    CustomCard(
        Modifier
            .padding(vertical = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = !expanded
                }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(0.98f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontSize = 14.sp,
                    color = WordyColor.colors.textCardPrimary,
                    style = WordyTypography.bodyLarge
                )
                Image(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = "open",
                    colorFilter = ColorFilter.tint(green800),
                    modifier = Modifier.rotate(rotation)
                )
            }
            AnimatedVisibility(
                visible = expanded,
            ) {
                if (expanded) {
                    DictionaryCardExpandedContent(
                        description = description,
                        title = title,
                        onEvent = onEvent,
                        context = context
                    )
                }
            }
        }
    }
}

@Composable
private fun DictionaryCardExpandedContent(
    description: String,
    title: String,
    onEvent: (DictionaryUiEvent) -> Unit,
    context: Context
) {
    Column {
        HorizontalDivider(
            color = red,
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.13f)
        )
        Text(
            description.ifEmpty { stringResource(R.string.no_description) },
            fontSize = 14.sp,
            color = WordyColor.colors.textCardPrimary,
            style = WordyTypography.bodyMedium
        )
        Row(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DictionaryImageButton(R.drawable.dict_search) {
                onEvent(DictionaryUiEvent.OnNavigateToInternetDictionary(title, context))
            }
            DictionaryImageButton(R.drawable.dict_share) {
                onEvent(
                    DictionaryUiEvent.OnShareWord(
                        context.getString(
                            R.string.share_text,
                            title,
                            description.ifEmpty { "" },
                            "https://www.rustore.ru/catalog/app/com.sinya.projects.wordle"
                        ),
                        context
                    )
                )
            }
            DictionaryImageButton(image = R.drawable.dict_reload) {
                onEvent(DictionaryUiEvent.OnReloadedDefinition(title, context))
            }
        }
    }
}
package com.sinya.projects.wordle.presentation.dictionary.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import com.sinya.projects.wordle.domain.model.DictionaryItem
import com.sinya.projects.wordle.presentation.dictionary.DictionaryEvent
import com.sinya.projects.wordle.ui.features.CustomCard
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import com.sinya.projects.wordle.ui.theme.green800

@Composable
fun DictionaryCard(
    item: DictionaryItem,
    onEvent: (DictionaryEvent) -> Unit,
    onOpenUrl: (String) -> Unit,
    onShare: (String, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) -90f else 90f,
        label = "arrow_rotation"
    )

    CustomCard(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.word,
                    fontSize = 14.sp,
                    color = WordyColor.colors.textCardPrimary,
                    style = WordyTypography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = "open",
                    colorFilter = ColorFilter.tint(green800),
                    modifier = Modifier.rotate(rotation)
                )
            }
            AnimatedVisibility(visible = expanded) {
                DictionaryCardExpandedContent(
                    item = item,
                    onEvent = onEvent,
                    onOpenUrl = onOpenUrl,
                    onShare = onShare
                )
            }
        }
    }
}

@Composable
private fun DictionaryCardExpandedContent(
    item: DictionaryItem,
    onEvent: (DictionaryEvent) -> Unit,
    onOpenUrl: (String) -> Unit,
    onShare: (String, String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing)
        ),
        label = "rotation"
    )

    val rotation = if (item.isLoading) angle else 0f

    Column {
        HorizontalDivider(
            color = WordyColor.colors.secondary,
            thickness = 1.dp,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(0.13f)
        )
        Text(
            item.description.ifEmpty { stringResource(R.string.no_description) },
            fontSize = 14.sp,
            color = WordyColor.colors.textCardPrimary,
            style = WordyTypography.bodyMedium
        )
        Row(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DictionaryImageButton(R.drawable.dict_search) {
                onOpenUrl(item.word)
            }
            DictionaryImageButton(R.drawable.dict_share) {
                onShare(item.word, item.description)
            }
            DictionaryImageButton(image = R.drawable.dict_reload, rotation) {
                onEvent(DictionaryEvent.OnReloadedDefinition(item))
            }
        }
    }
}
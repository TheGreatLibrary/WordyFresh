package com.sinya.projects.wordle.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.TypeKeyboards
import com.sinya.projects.wordle.ui.features.CheckedIcon
import com.sinya.projects.wordle.ui.features.CustomModalSheet
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyShapes
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun KeyboardModalSheet(
    currentKey: Int,
    onKeyboardSelect: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val keyboards = remember { TypeKeyboards.entries }
    val initialPage = remember { keyboards.indexOfFirst { it.code == currentKey }.coerceAtLeast(0) }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { keyboards.size } )

    CustomModalSheet(
        onDismissRequest = {
            onDismissRequest()
            onKeyboardSelect(keyboards[pagerState.currentPage].code)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 45.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.change_keyboard),
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                keyboards.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(WordyShapes.extraLarge)
                            .background(
                                if (pagerState.currentPage == index) WordyColor.colors.backPrimary
                                else WordyColor.colors.textCardSecondary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                pageSpacing = 16.dp
            ) { page ->
                KeyboardModeItem(
                    keyboards = keyboards[page],
                    isSelected = pagerState.currentPage == page,
                    onSelect = {}
                )
            }
        }
    }
}

@Composable
private fun KeyboardModeItem(
    keyboards: TypeKeyboards,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(230.dp)
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor =
            if (isSelected) WordyColor.colors.backPrimary.copy(alpha = 0.3f)
            else WordyColor.colors.textPrimary.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(keyboards.title),
                    style = MaterialTheme.typography.titleMedium,
                    color = WordyColor.colors.textCardPrimary,
                    modifier = Modifier.weight(1f)
                )
                CheckedIcon(isSelected)
            }
            Image(
                painter = painterResource(keyboards.res),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

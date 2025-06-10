package com.sinya.projects.wordle.screen.keyboard

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.domain.model.data.KeyboardItem
import com.sinya.projects.wordle.ui.features.CheckedIcon
import com.sinya.projects.wordle.ui.features.Header
import com.sinya.projects.wordle.ui.theme.WordleColor
import kotlinx.coroutines.flow.Flow

@Composable
fun KeyboardScreen(
    navigateToBackStack: () -> Unit,
    getKeyboard: (Context) -> Flow<Int>,
    boards: List<KeyboardItem>,
    onClick: (Context, Int) -> Unit
) {
    val context = LocalContext.current
    val codeKeyboard by getKeyboard(context).collectAsState(initial = 0)

    Column(
        Modifier
            .fillMaxSize()
            .background(color = WordleColor.colors.background)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp),

        ) {
        Header(
            title =stringResource(R.string.change_keyboard),
            trashVisible = false,
            navigateTo = navigateToBackStack
        )
        LazyColumn {
            items(boards.size) { index ->
                KeyboardModeItem(
                    mode = boards[index],
                    isSelected = boards[index].code == codeKeyboard,
                    onSelect = { onClick(context, boards[index].code) }
                )
            }
        }
    }
}


@Composable
fun KeyboardModeItem(
    mode: KeyboardItem,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor =
            if (isSelected) WordleColor.colors.backPrimary.copy(alpha = 0.3f)
            else WordleColor.colors.textPrimary.copy(alpha = 0.05f)
        )
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(mode.modeName),
                    style = MaterialTheme.typography.titleMedium,
                    color = WordleColor.colors.textCardPrimary,
                    modifier = Modifier.weight(1f)
                )
                CheckedIcon(isSelected)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(mode.modeDescription),
                style = MaterialTheme.typography.bodyMedium,
                color = WordleColor.colors.textCardSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(mode.previewRes),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Preview
@Composable
private fun KeyboardScreenPreview() {
    KeyboardScreen(
        navigateToBackStack = {  },
        getKeyboard = { context -> AppDataStore.getKeyboardMode(context) },
        boards = AppKeyboards.supported,
        onClick = { context, code -> },
    )
}
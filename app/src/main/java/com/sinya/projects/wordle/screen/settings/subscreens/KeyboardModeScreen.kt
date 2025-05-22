package com.sinya.projects.wordle.screen.settings.subscreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.data.local.datastore.AppDataStore
import com.sinya.projects.wordle.domain.model.data.KeyboardItem
import com.sinya.projects.wordle.navigation.Header
import com.sinya.projects.wordle.ui.components.AppKeyboards
import com.sinya.projects.wordle.ui.components.CheckedIcon
import com.sinya.projects.wordle.ui.theme.WordleColor
import com.sinya.projects.wordle.ui.theme.gray600
import com.sinya.projects.wordle.ui.theme.green800
import com.sinya.projects.wordle.ui.theme.white
import kotlinx.coroutines.launch

@Composable
fun KeyboardModeScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val codeKeyboard by AppDataStore.getKeyboardMode(context).collectAsState(initial = 0)
    val keyboardItemList = AppKeyboards.supported

    Column(
        Modifier
            .fillMaxSize()
            .background(color = WordleColor.colors.background)
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp),

        ) {
        Header(stringResource(R.string.change_keyboard), false, navController)
        LazyColumn {
            items(keyboardItemList.size) { index ->
                KeyboardModeItem(
                    mode = keyboardItemList[index],
                    isSelected = keyboardItemList[index].code == codeKeyboard,
                    onSelect = {
                        coroutineScope.launch {
                            AppDataStore.setKeyboardMode(context, keyboardItemList[index].code)
                        }
                    }
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
    val backgroundColor =
        if (isSelected) WordleColor.colors.backPrimary.copy(alpha = 0.2f) else WordleColor.colors.backgroundCard.copy(alpha = 0.1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(mode.modeName),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                CheckedIcon(isSelected)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(mode.modeDescription),
                style = MaterialTheme.typography.bodyMedium,
                color = WordleColor.colors.textColorMkI
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
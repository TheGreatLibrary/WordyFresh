package com.sinya.projects.wordle.presentation.settings.sheets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageModalSheet(
    currentLang: String,
    onLanguageSelect: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val languages = TypeLanguages.entries

    ModalBottomSheet(
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = WordyColor.colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.change_lang),
                color = WordyColor.colors.textPrimary,
                style = WordyTypography.titleLarge,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            languages.forEach {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelect(it.code) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = it.code == currentLang,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = WordyColor.colors.primary
                        ),
                        onClick = { onLanguageSelect(it.code) }
                    )
                    Text(
                        text = Locale(it.code).displayName.replaceFirstChar { char ->
                            char.uppercaseChar()
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = WordyColor.colors.textPrimary
                    )
                }
            }
        }
    }
}

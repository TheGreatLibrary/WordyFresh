package com.sinya.projects.wordle.ui.features

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sinya.projects.wordle.domain.enums.TypeLanguages
import com.sinya.projects.wordle.domain.model.RadioItem
import com.sinya.projects.wordle.ui.theme.WordyColor
import com.sinya.projects.wordle.ui.theme.WordyTypography

@Composable
fun <T> SortBlock(
    title: String,
    selectedOption: T?,
    radioOptions: List<RadioItem<T>>,
    onOptionSelected: (T) -> Unit
) {

    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space = 8.dp)
    ) {
        item {
            Text(
                text = title,
                style = WordyTypography.bodyLarge,
                color = WordyColor.colors.textPrimary
            )
        }

        items(
            items = radioOptions
        ) { item ->
            val isSelected = selectedOption == item.value

            RoundedButton(
                elevation = 10,
                modifier = Modifier.padding(bottom = 4.dp).height(35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) {
                        WordyColor.colors.primary
                    } else {
                        WordyColor.colors.backgroundCard
                    },
                    contentColor = if (isSelected) {
                        WordyColor.colors.textOnColorCard
                    } else {
                        WordyColor.colors.textCardPrimary
                    }
                ),
                contentPadding = PaddingValues(vertical = 0.dp, horizontal = 5.dp),
                onClick = { item.value?.let { onOptionSelected(it) } }
            ) {
                item.text?.let {
                    val res = TypeLanguages.getShortName(item.text)

                    Text(
                        text = if (res!=null) stringResource(res) else item.text,
                        fontSize = 14.sp,
                        style = WordyTypography.bodyMedium
                    )
                }
            }
        }
    }
}
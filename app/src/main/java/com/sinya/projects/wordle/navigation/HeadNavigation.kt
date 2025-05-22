package com.sinya.projects.wordle.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.ui.components.ImageButton
import com.sinya.projects.wordle.ui.theme.WordleTypography
import com.sinya.projects.wordle.ui.theme.white

@Composable
fun Header(name: String, trashVisible: Boolean, navController: NavController) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ImageButton(R.drawable.arrow_back, modifier = Modifier.size(32.dp)) { navController.popBackStack() }
        Text(name, fontSize = 24.sp, color = white, style = WordleTypography.titleLarge)
        Box(Modifier.size(32.dp)) {
            if (trashVisible) {
                ImageButton(R.drawable.ic_trash, modifier = Modifier.size(32.dp).scale(0.9f)) { }
            }
        }
    }
}

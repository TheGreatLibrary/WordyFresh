package com.sinya.projects.wordle.screen.achieve

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sinya.projects.wordle.R
import com.sinya.projects.wordle.navigation.Header

@Composable
fun AchieveScreen(navController: NavController) {
//    viewModel = AchieveViewModel = viewModel(),

    Column(
        Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 50.dp, end = 16.dp, bottom = 7.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Header(stringResource(R.string.achievements), false, navController)
        LazyColumn(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

        }
    }
}

@Composable
fun AchieveContainer() {

}

@Composable
fun AchieveBox() {

}
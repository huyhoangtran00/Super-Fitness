package com.example.superfitness.ui.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val runsList = viewModel.allRuns.collectAsStateWithLifecycle().value

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = runsList,
            key = {item -> item.id}
        ) {item ->


        }
    }
}

@Composable
fun RunningCard(
    modifier: Modifier = Modifier
) {
    Card {

    }
}
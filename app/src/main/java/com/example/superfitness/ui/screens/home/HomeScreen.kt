package com.example.superfitness.ui.screens.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.home.components.RunningCard
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.HomeViewModel

object HomeDestination : NavigationDestination {
    override val route = "home screen"
    override val titleRes = R.string.home
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onRunItemClick: (Int) -> Unit
) {
    val runsList = viewModel.allRuns.collectAsStateWithLifecycle().value

    if (runsList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "There is no running activity",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
    LazyColumn(
        modifier = Modifier.padding(top = 32.dp, start = 4.dp, end = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = runsList.reversed(),
            key = { item -> item.id }
        ) {item ->

            RunningCard(
                modifier = Modifier.fillMaxWidth(),
                runItem = item,
                onClick = {
                    onRunItemClick(item.id)
                }
            )
        }
    }
}
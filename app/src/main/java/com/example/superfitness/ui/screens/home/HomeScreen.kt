package com.example.superfitness.ui.screens.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superfitness.R
import com.example.superfitness.ui.navigation.NavigationDestination
import com.example.superfitness.ui.screens.BarScreenDestination
import com.example.superfitness.ui.screens.BarScreen
import com.example.superfitness.ui.screens.home.components.RunningCard
import com.example.superfitness.viewmodel.AppViewModelProvider
import com.example.superfitness.viewmodel.HomeViewModel
import com.example.superfitness.viewmodel.RunViewModel
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel

object HomeDestination : NavigationDestination {
    override val route = "home screen"
    override val titleRes = R.string.home
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onRunItemClick: (Int) -> Unit,
    runViewModel: RunViewModel,
    waterIntakeViewModel: WaterIntakeViewModel
) {
    val runsList = viewModel.allRuns.collectAsStateWithLifecycle().value.reversed()
    var selectedTab by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Hoạt động") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Thống kê") }
            )
        }
        when (selectedTab) {
            0 -> {
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
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(
                            items = runsList,
                            key = { item -> item.id }
                        ) { item ->
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
            }
            1 -> {
                BarScreen(
                    runViewModel = runViewModel,
                    waterIntakeViewModel = waterIntakeViewModel
                )
            }
        }
    }
}
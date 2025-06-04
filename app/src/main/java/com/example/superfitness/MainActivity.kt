package com.example.superfitness

import RunDetailsScreen
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.ui.screens.run.RunDestination
import com.example.superfitness.ui.screens.run.RunScreen
import com.example.superfitness.ui.screens.home.HomeDestination
import com.example.superfitness.ui.screens.home.HomeScreen
import com.example.superfitness.ui.screens.weather.WeatherDestination
import com.example.superfitness.ui.screens.weather.WeatherScreen
import com.example.superfitness.utils.RED
import com.google.accompanist.permissions.ExperimentalPermissionsApi

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                MainScreen(
                    openSettings = ::openAppSettings
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    openSettings: () -> Unit
) {

    val navController = rememberNavController()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when(navBackStackEntry?.destination?.route) {
        RunDestination.route,
        RunDetailsDestination.routeWithArgs -> {
            bottomBarState.value = false
        }
        else -> {
            bottomBarState.value = true
        }
    }

    Scaffold(
        bottomBar = { CustomBottomNavigationBar(
            navController = navController,
            bottomBarState = bottomBarState
        ) },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Home Screen
            composable(
                route = HomeDestination.route
            ) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onRunItemClick = {
                        navController.navigate(
                            "${RunDetailsDestination.route}/${it}"
                        )
                    }
                )
            }


            // Run Screen
            composable(
                route = RunDestination.route,
                enterTransition = {
                    slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(durationMillis = 500)
                    )
                },
            ) {
                RunScreen(
                    onCloseScreenClick = {
                        navController.navigateSingleTopTo(HomeDestination.route)
                    },
                    openSettings = openSettings,
                    modifier = Modifier.fillMaxSize()
            ) }

            // Run Details Screen
            composable(
                route = RunDetailsDestination.routeWithArgs,
                arguments = RunDetailsDestination.arguments,
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = tween(durationMillis = 800)
                    )
                }
            ) {navBackStackEntry ->
                RunDetailsScreen(
                    modifier = Modifier.fillMaxSize(),
                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }

            // Weather Screen
            composable(
                route = WeatherDestination.route
            ) {
                WeatherScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>
) {
    // Get the current back stack entry as State
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Fetch the current destination
    val currentDestination = navBackStackEntry?.destination
    val currentRoute  = currentDestination?.route

    val navBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = Color(RED.toColorInt()),
        selectedTextColor = Color(RED.toColorInt()),
        indicatorColor = Color("#FFE5E0".toColorInt()),
    )

    AnimatedVisibility(
        visible = bottomBarState.value,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            NavigationBar(
                containerColor = Color.White
            ) {
                // Trang chủ
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
                    label = { Text("Home", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == HomeDestination.route,
                    onClick = { navController.navigate(HomeDestination.route) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DirectionsRun, contentDescription = "Activity") },
                    label = { Text("Activity", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == RunDestination.route,
                    onClick = { navController.navigateSingleTopTo(route = RunDestination.route) },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.WbSunny, contentDescription = "Weather") },
                    label = { Text("Weather", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == WeatherDestination.route,
                    onClick = { navController.navigateSingleTopTo(route = WeatherDestination.route) },
                    colors = navBarItemColors
                )
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route) {
        // most only one copy of a destination on the top of the back stack
        launchSingleTop = true
        // pop up to the Start destination of the graph
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        restoreState = true
    }
}



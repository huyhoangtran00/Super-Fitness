package com.example.superfitness

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.superfitness.data.local.dao.AirQualityDao
import com.example.superfitness.data.local.dao.WeatherDao
import com.example.superfitness.data.local.db.AppDatabase
import com.example.superfitness.data.local.db.dao.UserProfileDao
import com.example.superfitness.data.local.db.dao.WaterIntakeDao
import com.example.superfitness.data.remote.api.AirQualityApi
import com.example.superfitness.data.remote.api.WeatherApi
import com.example.superfitness.data.repository.AirQualityRepository
import com.example.superfitness.data.repository.UserProfileRepository
import com.example.superfitness.data.repository.WeatherRepository
import com.example.superfitness.repository.DefaultILocationTracker
import com.example.superfitness.repository.OfflineRunRepository
import com.example.superfitness.repository.WaterIntakeRepository
import com.example.superfitness.ui.WeatherCard
import com.example.superfitness.ui.screens.BarScreenDestination
import com.example.superfitness.ui.screens.BarScreen
import com.example.superfitness.ui.screens.run.RunDestination
import com.example.superfitness.ui.screens.run.RunScreen
import com.example.superfitness.ui.screens.WaterTrackingApp
import com.example.superfitness.ui.viewmodel.UserProfileViewModel
import com.example.superfitness.viewmodel.UserProfileViewModelFactory
import com.example.superfitness.ui.screens.UserProfileInputScreen
import com.example.superfitness.ui.screens.home.HomeDestination
import com.example.superfitness.ui.screens.home.HomeScreen
import com.example.superfitness.ui.screens.runningdetails.RunDetailsDestination
import com.example.superfitness.ui.screens.runningdetails.RunDetailsScreen
import com.example.superfitness.ui.viewmodel.WaterIntakeViewModel
import com.example.superfitness.viewmodel.WaterIntakeViewModelFactory
import com.example.superfitness.viewmodel.WeatherViewModel
import com.example.superfitness.viewmodel.WeatherViewModelFactory
import com.example.superfitness.viewmodel.RunViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.ExperimentalCoroutinesApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.example.superfitness.location.AndroidLocationManager
import com.example.superfitness.ui.settings.UserProfileScreen
import com.example.superfitness.utils.PreferencesManager
import com.example.superfitness.utils.RED

class MainActivity : ComponentActivity() {
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var waterIntakeViewModel: WaterIntakeViewModel
    private lateinit var weatherViewModel : WeatherViewModel
    private lateinit var runViewModel: RunViewModel
    private lateinit var preferencesManager: PreferencesManager

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        preferencesManager = PreferencesManager(this)

        val db = AppDatabase.getDatabase(this)
        val userProfileDao: UserProfileDao = db.userProfileDao()
        val userProfileRepository = UserProfileRepository(userProfileDao)
        val userFactory = UserProfileViewModelFactory(userProfileRepository)
        userProfileViewModel = ViewModelProvider(this, userFactory).get(UserProfileViewModel::class.java)

        val waterIntakeDao: WaterIntakeDao = db.waterIntakeDao()
        val waterIntakeRepository = WaterIntakeRepository(waterIntakeDao)
        val waterFactory = WaterIntakeViewModelFactory(waterIntakeRepository)
        waterIntakeViewModel = ViewModelProvider(this, waterFactory).get(WaterIntakeViewModel::class.java)

        // Initialize RunRepository and RunViewModel
        val runDao = db.runDao() 
        val runRepository = OfflineRunRepository(runDao)
        val locationManager = AndroidLocationManager(applicationContext, LocationServices.getFusedLocationProviderClient(this))
        runViewModel = RunViewModel(locationManager, runRepository)

        val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val weatherApi = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
        val weatherDao: WeatherDao = db.weatherDao()

        val weatherRepository = WeatherRepository(weatherApi, weatherDao,connectionManager)

        val airQualityApi = Retrofit.Builder()
            .baseUrl("https://air-quality-api.open-meteo.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(AirQualityApi::class.java)

        val airQualityDao: AirQualityDao = db.airQualityDao()
        val airQualityRepository = AirQualityRepository(airQualityApi,airQualityDao,connectionManager)
        val locationTracker = DefaultILocationTracker(LocationServices.getFusedLocationProviderClient(this), application)

        val weatherFactory = WeatherViewModelFactory(weatherRepository, airQualityRepository, locationTracker)
        weatherViewModel = ViewModelProvider(this,weatherFactory)[WeatherViewModel::class.java]

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            weatherViewModel.loadWeatherInfo()
            weatherViewModel.loadForecastWeatherInfo()
            weatherViewModel.loadAirQualityInfo()
        }
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        ))
        setContent {
            MaterialTheme {
                AppContent(
                    userProfileViewModel = userProfileViewModel,
                    waterIntakeViewModel = waterIntakeViewModel,
                    weatherViewModel = weatherViewModel,
                    runViewModel = runViewModel,
                    openSettings = ::openAppSettings,
                    preferencesManager = preferencesManager
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppContent(
    userProfileViewModel: UserProfileViewModel,
    waterIntakeViewModel: WaterIntakeViewModel,
    weatherViewModel: WeatherViewModel,
    runViewModel: RunViewModel,
    preferencesManager : PreferencesManager,
    openSettings: () -> Unit
) {

    val navController = rememberNavController()
    val hasUserProfile by userProfileViewModel.hasUserProfile.collectAsState()
    val isLoading by userProfileViewModel.isLoading.collectAsState()

    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LaunchedEffect(hasUserProfile, isLoading) {
        if (!isLoading && hasUserProfile) {
            navController.navigate(HomeDestination.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    when(navBackStackEntry?.destination?.route) {
        RunDestination.route,
        "profile_input",
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
            startDestination =  if (hasUserProfile) HomeDestination.route else "profile_input",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("profile_input") {
                UserProfileInputScreen(
                    userProfileViewModel = userProfileViewModel,
                    onProfileSaved = {
                        navController.navigate(HomeDestination.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable(
                route = HomeDestination.route
            ) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    onRunItemClick = {
                        navController.navigate(
                            "${RunDetailsDestination.route}/${it}"
                        )
                    },
                    runViewModel = runViewModel,
                    waterIntakeViewModel = waterIntakeViewModel
                )
            }
            composable("water") {
                WaterTrackingApp(waterIntakeViewModel,  preferencesManager, userProfileViewModel)
            }
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
                    onGoToSettings = {
                        navController.navigateSingleTopTo("settings")
                    },
                    modifier = Modifier.fillMaxSize()
            ) }
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
            composable(
                route = BarScreenDestination.route
            ) {
                BarScreen(
                    runViewModel = runViewModel,
                    waterIntakeViewModel = waterIntakeViewModel
                )
            }
            composable("weather") {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent)
                    ) {
                        WeatherCard(
                            state = weatherViewModel.state,
                            forecastState = weatherViewModel.stateForecastWeather,
                            airQualityState = weatherViewModel.airQualityState
                        )
                    }
                    if(weatherViewModel.state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    weatherViewModel.state.error?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            composable("settings") {
                UserProfileScreen(userProfileViewModel)
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
                containerColor = Color.White,
            ) { // Dùng NavigationBar của M3 thay cho Row
                // Trang chủ
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Trang chủ") },
                    label = { Text("Trang chủ", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == HomeDestination.route,
                    onClick = { navController.navigateSingleTopTo(HomeDestination.route) },
                    colors = navBarItemColors
                )

                // Hoạt động
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.LocalDrink, contentDescription = "Water Reminder") },
                    label = { Text("Uống nươc", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == "water",
                    onClick = { navController.navigateSingleTopTo("water") },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DirectionsRun, contentDescription = "Activity") },
                    label = { Text("Hoạt động", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == RunDestination.route,
                    onClick = { navController.navigateSingleTopTo(route = RunDestination.route) },
                    colors = navBarItemColors
                )


                NavigationBarItem(
                    icon = { Icon(Icons.Filled.WbSunny, contentDescription = "Weather") },
                    label = { Text("Thời tiết", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == "weather",
                    onClick = { navController.navigateSingleTopTo("weather") },
                    colors = navBarItemColors
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                    label = { Text("Hồ sơ ", style = MaterialTheme.typography.labelSmall) },
                    selected = currentRoute == "settings",
                    onClick = { navController.navigateSingleTopTo("settings") },
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



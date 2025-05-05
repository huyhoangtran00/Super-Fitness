import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.superfitness.ui.WeatherCard
import com.example.superfitness.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel = hiltViewModel()
) {
    val state = weatherViewModel.state
    val forecastState = weatherViewModel.stateForecastWeather
    val airQualityState = weatherViewModel.airQualityState

    LaunchedEffect(Unit) {
        weatherViewModel.loadWeatherInfo()
        weatherViewModel.loadAirQualityInfo()
        weatherViewModel.loadForecastWeatherInfo()
    }

    WeatherCard(
        state = state,
        forecastState = forecastState,
        airQualityState = airQualityState
    )
} 
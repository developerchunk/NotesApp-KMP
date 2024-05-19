import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.MongoDB
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screens.home.HomeScreen
import presentation.screens.home.HomeViewModel
import presentation.screens.task.TaskViewModel

val lightRedColor = Color(color = 0xFFF57D88)
val darkRedColor = Color(color = 0xFF77000B)
val lightBackground = Color(0xFFF9FCFF)
val darkBackground = Color(0xFF16212B)
val lightCardBackground = Color(0xFFFFD8E4)
val darkCardBackground = Color(0xFFD0BCFF)

@Composable
@Preview
fun App() {

    initializeKoin()

    val lightColor = lightColorScheme(
        primary = lightRedColor,
        onPrimary = darkRedColor,
        primaryContainer = lightRedColor,
        onPrimaryContainer = darkRedColor,
        background = lightBackground,
        onBackground = darkBackground,
        surface = lightCardBackground,
        onSurface = darkCardBackground
    )

    val darkColor = darkColorScheme(
        primary = darkRedColor,
        onPrimary = lightRedColor,
        primaryContainer = darkRedColor,
        onPrimaryContainer = lightRedColor,
        background = darkBackground,
        onBackground = lightBackground,
        surface = darkCardBackground,
        onSurface = lightCardBackground,
    )

    val colors by mutableStateOf(
        if (isSystemInDarkTheme()) darkColor else lightColor
    )

    MaterialTheme(colorScheme = colors) {

        Navigator(HomeScreen()) {
            SlideTransition(it)
        }

    }
}

val mongoModule = module {

    single { MongoDB() }
    factory { HomeViewModel(get()) }
    factory { TaskViewModel(get()) }

}

fun initializeKoin() {
    startKoin {
        modules(mongoModule)
    }
}
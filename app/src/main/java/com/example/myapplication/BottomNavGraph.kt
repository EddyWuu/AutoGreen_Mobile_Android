import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import screens.ControlsScreen
import screens.ProfileScreen
import screens.HistoryScreen
import screens.HomeScreen

@Composable
fun BottomNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Home.route
    ) {

        composable(route = BottomNavScreen.Home.route) {
            HomeScreen()
        }
        composable(route = BottomNavScreen.Controls.route) {
            ControlsScreen()
        }
        composable(route = BottomNavScreen.History.route) {
            HistoryScreen()
        }
        composable(route = BottomNavScreen.Profile.route) {
            ProfileScreen()
        }
    }
}
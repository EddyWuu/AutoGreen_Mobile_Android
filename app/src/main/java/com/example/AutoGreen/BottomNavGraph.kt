
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.AutoGreen.network.viewmodels.ControlsViewModel
import com.example.AutoGreen.viewmodels.HomeViewModel
import screens.*

@Composable
fun BottomNavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val controlViewModel: ControlsViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Home.route
    ) {

        composable(route = BottomNavScreen.Home.route) {
            HomeScreen(viewModel = homeViewModel)
        }
        composable(route = BottomNavScreen.Controls.route) {
            ControlsScreen(viewModel = controlViewModel)
        }
        composable(route = BottomNavScreen.History.route) {
            HistoryScreen()
        }
        composable(route = BottomNavScreen.Profile.route) {
            ProfileScreen()
        }
    }
}
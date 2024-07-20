import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object  Home: BottomNavScreen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )
    object  Profile: BottomNavScreen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )
    object  Controls: BottomNavScreen(
        route = "controls",
        title = "Controls",
        icon = Icons.Default.DateRange
    )
    object  History: BottomNavScreen(
        route = "history",
        title = "History",
        icon = Icons.Default.ShoppingCart
    )
}
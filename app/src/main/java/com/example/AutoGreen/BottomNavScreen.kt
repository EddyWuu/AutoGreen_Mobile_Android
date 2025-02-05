import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.AutoGreen.R

sealed class BottomNavScreen(
    val route: String,
    val title: String,
    val iconRes: Int
) {
    object  Home: BottomNavScreen(
        route = "home",
        title = "Home",
        iconRes = R.drawable.home_svgrepo_com
    )
//    object  Profile: BottomNavScreen(
//        route = "profile",
//        title = "Profile",
//        iconRes = R.drawable.profile_icon
//    )
    object  Controls: BottomNavScreen(
        route = "controls",
        title = "Controls",
        iconRes = R.drawable.controls_icon
    )
    object  History: BottomNavScreen(
        route = "history",
        title = "History",
        iconRes = R.drawable.history_svgrepo_com
    )

    @Composable
    fun iconPainter(): Painter {
        return painterResource(id = iconRes)
    }
}
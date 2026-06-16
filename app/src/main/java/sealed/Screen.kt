package sealed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Search: Screen("search_screen", "Search", Icons.Default.Search)
    object Favorites: Screen("favorites_screen", "Favorites", Icons.Default.Favorite)
}
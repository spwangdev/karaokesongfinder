package sealed

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Search: Screen("search_screen", "Search", Icons.Default.Search)
    object Latest: Screen("latest_screen", "Latest", Icons.Default.NewReleases)
    object Favorites: Screen("favorites_screen", "Favorites", Icons.Default.Favorite)
    object Settings: Screen("settings_screen", "Settings", Icons.Default.Settings)
}
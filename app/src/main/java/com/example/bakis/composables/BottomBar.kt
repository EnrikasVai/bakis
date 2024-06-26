package com.example.bakis.composables

import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun CustomBottomNavigationBar(
    navController: NavController,
    items: List<String>,
    icons: List<ImageVector>
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selectedItem = when (currentRoute) {
        "home" -> 0
        "health" -> 1
        "profile" -> 2
        else -> 0
    }

    BottomAppBar(containerColor = Color(0xFF1a1a1a), modifier = Modifier.height(70.dp)) {
        NavigationBar(
            containerColor = Color(0xFF1a1a1a)
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedItem
                NavigationBarItem(
                    icon = {
                        Icon(icons[index], contentDescription = item,
                            tint = if (isSelected) Color(0xFFff681d) else Color.White)
                    },
                    label = {
                        Text(text = item, color = if (isSelected) Color(0xFFff681d)  else Color.White)
                    },
                    selected = isSelected,
                    onClick = {
                        // Navigate based on the item clicked
                        val route = when (index) {
                            0 -> "home"
                            1 -> "health"
                            2 -> "profile"
                            else -> "home"
                        }
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color(0xFF1a1a1a)
                )
                )
            }
        }
    }
}

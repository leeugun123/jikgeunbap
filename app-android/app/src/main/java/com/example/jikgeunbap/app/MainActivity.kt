package com.example.jikgeunbap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.jikgeunbap.app.presentation.AppStartViewModel
import com.example.jikgeunbap.app.presentation.Screen
import com.example.jikgeunbap.app.presentation.theme.JikGeunBapTheme
import com.example.jikgeunbap.app.presentation.theme.WarmCream
import com.example.jikgeunbap.app.presentation.theme.WarmDivider
import com.example.jikgeunbap.app.presentation.theme.WarmOrange
import com.example.jikgeunbap.app.presentation.theme.WarmBrownMid
import com.example.jikgeunbap.app.presentation.ui.favorites.FavoritesScreen
import com.example.jikgeunbap.app.presentation.ui.history.HistoryScreen
import com.example.jikgeunbap.app.presentation.ui.main.MainScreen
import com.example.jikgeunbap.app.presentation.ui.workplace.WorkplaceScreen
import dagger.hilt.android.AndroidEntryPoint

private data class NavItem(
    val screen: Screen,
    val emoji: String,
    val label: String
)

private val BOTTOM_NAV_ITEMS = listOf(
    NavItem(Screen.Main,      "🍽️", "추천"),
    NavItem(Screen.History,   "📋", "히스토리"),
    NavItem(Screen.Favorites, "❤️", "찜")
)

private val BOTTOM_NAV_ROUTES = BOTTOM_NAV_ITEMS.map { it.screen.route }.toSet()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JikGeunBapTheme {
                val appStartViewModel: AppStartViewModel = hiltViewModel()
                val startRoute by appStartViewModel.startRoute.collectAsState()

                if (startRoute == null) {
                    Box(
                        modifier         = Modifier
                            .fillMaxSize()
                            .background(WarmCream),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🍱", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            CircularProgressIndicator(color = WarmOrange)
                        }
                    }
                } else {
                    val navController = rememberNavController()
                    val currentBackStack by navController.currentBackStackEntryAsState()
                    val currentRoute = currentBackStack?.destination?.route
                    val showBottomBar = currentRoute in BOTTOM_NAV_ROUTES

                    Scaffold(
                        modifier      = Modifier.fillMaxSize(),
                        containerColor = WarmCream,
                        bottomBar = {
                            if (showBottomBar) {
                                WarmBottomBar(
                                    items        = BOTTOM_NAV_ITEMS,
                                    currentRoute = currentRoute,
                                    onItemClick  = { screen ->
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState    = true
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController    = navController,
                            startDestination = startRoute!!,
                            modifier         = Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Main.route) {
                                MainScreen(
                                    onOpenWorkplace = {
                                        navController.navigate(Screen.Workplace.route)
                                    }
                                )
                            }

                            composable(Screen.History.route) {
                                HistoryScreen()
                            }

                            composable(Screen.Favorites.route) {
                                FavoritesScreen()
                            }

                            composable(Screen.Workplace.route) {
                                WorkplaceScreen(
                                    onBack  = { navController.popBackStack() },
                                    onSaved = { navController.popBackStack() }
                                )
                            }

                            composable(Screen.WorkplaceOnboarding.route) {
                                WorkplaceScreen(
                                    onBack  = null,
                                    onSaved = {
                                        navController.navigate(
                                            Screen.Main.route,
                                            navOptions {
                                                popUpTo(Screen.WorkplaceOnboarding.route) {
                                                    inclusive = true
                                                }
                                                launchSingleTop = true
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WarmBottomBar(
    items: List<NavItem>,
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    Column {
        HorizontalDivider(color = WarmDivider, thickness = 1.dp)
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.screen.route
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier            = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onItemClick(item.screen) }
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text     = item.emoji,
                        fontSize = if (isSelected) 24.sp else 22.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text       = item.label,
                        fontSize   = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isSelected) WarmOrange else WarmBrownMid
                    )
                }
            }
        }
    }
}

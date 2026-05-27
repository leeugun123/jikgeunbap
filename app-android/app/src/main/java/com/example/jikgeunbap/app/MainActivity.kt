package com.example.jikgeunbap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.jikgeunbap.app.presentation.AppStartViewModel
import com.example.jikgeunbap.app.presentation.Screen
import com.example.jikgeunbap.app.presentation.theme.JikGeunBapTheme
import com.example.jikgeunbap.app.presentation.theme.WarmCream
import com.example.jikgeunbap.app.presentation.theme.WarmOrange
import com.example.jikgeunbap.app.presentation.ui.main.MainScreen
import com.example.jikgeunbap.app.presentation.ui.workplace.WorkplaceScreen
import dagger.hilt.android.AndroidEntryPoint

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
                    LoadingScreen()
                } else {
                    val navController = rememberNavController()
                    NavHost(
                        navController    = navController,
                        startDestination = startRoute!!,
                        modifier         = Modifier
                            .fillMaxSize()
                            .background(WarmCream)
                    ) {
                        composable(Screen.Main.route) {
                            MainScreen(
                                onOpenWorkplace = {
                                    navController.navigate(Screen.Workplace.route)
                                }
                            )
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

@androidx.compose.runtime.Composable
private fun LoadingScreen() {
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
}

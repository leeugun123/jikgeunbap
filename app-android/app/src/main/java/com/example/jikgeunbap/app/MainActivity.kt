package com.example.jikgeunbap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.jikgeunbap.app.presentation.AppStartViewModel
import com.example.jikgeunbap.app.presentation.Screen
import com.example.jikgeunbap.app.presentation.ui.main.MainScreen
import com.example.jikgeunbap.app.presentation.ui.workplace.WorkplaceScreen
import com.example.jikgeunbap.app.presentation.theme.JikGeunBapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JikGeunBapTheme {
                val appStartViewModel: AppStartViewModel = hiltViewModel()
                val startRoute = appStartViewModel.startRoute.collectAsState().value

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (startRoute == null) {
                        Text(
                            text = "로딩 중...",
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = startRoute,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = Screen.Main.route) {
                                MainScreen(
                                    onOpenWorkplace = {
                                        navController.navigate(Screen.Workplace.route)
                                    }
                                )
                            }

                            composable(route = Screen.Workplace.route) {
                                WorkplaceScreen(
                                    onBack = { navController.popBackStack() },
                                    onSaved = { navController.popBackStack() }
                                )
                            }

                            composable(route = Screen.WorkplaceOnboarding.route) {
                                WorkplaceScreen(
                                    onBack = null,
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
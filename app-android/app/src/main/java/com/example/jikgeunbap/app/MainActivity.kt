package com.example.jikgeunbap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Main.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 메인 화면
                        composable(route = Screen.Main.route) {
                            MainScreen(
                                onOpenWorkplace = {
                                    navController.navigate(Screen.Workplace.route)
                                }
                            )
                        }

                        // 작업장 화면
                        composable(route = Screen.Workplace.route) {
                            WorkplaceScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
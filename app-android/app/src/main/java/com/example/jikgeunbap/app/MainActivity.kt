package com.example.jikgeunbap.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.jikgeunbap.app.ui.screen.main.MainScreen
import com.example.jikgeunbap.app.ui.screen.workplace.WorkplaceScreen
import com.example.jikgeunbap.app.ui.theme.JikGeunBapTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JikGeunBapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val screen = remember { mutableStateOf("main") }

                    when (screen.value) {
                        "workplace" -> WorkplaceScreen(
                            modifier = Modifier.padding(innerPadding),
                            onBack = { screen.value = "main" }
                        )
                        else -> MainScreen(
                            modifier = Modifier.padding(innerPadding),
                            onOpenWorkplace = { screen.value = "workplace" }
                        )
                    }
                }
            }
        }
    }
}


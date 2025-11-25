package com.example.jikgeunbap.app.ui.screen.ai

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AiLunchRecommendScreen(
    modifier: Modifier = Modifier,
    viewModel: AiLunchRecommendViewModel = hiltViewModel()
) {
    var prompt by remember { mutableStateOf("") }
    val result = viewModel.recommendResult.collectAsState().value

    Column(modifier = modifier) {
        TextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("점심 한 마디! 예) 매운 거 땡김, 가볍게 먹고파") },
            modifier = Modifier.fillMaxWidth()
        )
        Box(modifier = Modifier
                .padding(top = 8.dp)
                .clickable {

                }) {
            Text("AI 점심 추천 받기")
        }

        result?.let {
            Text(text = "추천 가게: ${it.restaurantName}", modifier = Modifier.padding(top = 16.dp))
            Text(text = "설명: ${it.description}")
            it.menuSuggest?.let { menu -> Text("추천 메뉴: $menu") }
        }
    }
}

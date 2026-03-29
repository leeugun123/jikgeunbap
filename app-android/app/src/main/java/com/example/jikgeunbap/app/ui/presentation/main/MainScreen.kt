package com.example.jikgeunbap.app.ui.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jikgeunbap.domain.model.Restaurant

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onOpenWorkplace: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val restaurant = viewModel.restaurant.collectAsState().value
    val error = viewModel.error.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9FB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "오늘의 점심 랜덤 추천",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color(0xFF333366)
            )

            Spacer(modifier = Modifier.height(36.dp))

            RestaurantCard(restaurant = restaurant)

            Spacer(modifier = Modifier.height(38.dp))

            DiceButton(onClick = { viewModel.recommend() })

            if (error != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = error,
                    color = Color(0xFFCC3344),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(onClick = onOpenWorkplace) {
                Text("직장 위치 설정")
            }
        }
    }
}

@Composable
private fun RestaurantCard(restaurant: Restaurant?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .width(290.dp)
            .height(210.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            if (restaurant != null) {
                RestaurantCardContent(restaurant = restaurant)
            } else {
                Text("주사위를 굴려보세요!", color = Color(0xFF7C7C97))
            }
        }
    }
}

@Composable
private fun RestaurantCardContent(restaurant: Restaurant) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFD2E4FE)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🍴", fontSize = 32.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(restaurant.category, color = Color(0xFF7C7C97), fontSize = 13.sp)

        Spacer(modifier = Modifier.height(7.dp))

        Row(horizontalArrangement = Arrangement.Center) {
            restaurant.tags.take(2).forEach { tag ->
                RestaurantTag(tag = tag)
            }
        }
    }
}

@Composable
private fun RestaurantTag(tag: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE6E6F0))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text = "#$tag", fontSize = 12.sp, color = Color(0xFF333366))
    }
}

@Composable
private fun DiceButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFF4F61E9))
            .clickable(onClick = onClick)
            .padding(horizontal = 32.dp, vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎲", fontSize = 21.sp)
            Spacer(modifier = Modifier.width(7.dp))
            Text(
                text = "주사위 굴리기",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}
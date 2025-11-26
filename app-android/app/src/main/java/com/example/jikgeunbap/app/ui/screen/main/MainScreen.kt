package com.example.jikgeunbap.app.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val restaurant = viewModel.restaurant.collectAsState().value

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
            // 추천 카드
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .width(290.dp)
                    .height(210.dp),
                elevation = CardDefaults.cardElevation(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    if (restaurant != null) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // 이미지 or 기본 아이콘
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFD2E4FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                // PlaceHolder (아이콘/썸네일)
                                Text(text = "🍴", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(restaurant.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(restaurant.category, color = Color(0xFF7C7C97), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(7.dp))
                            // 태그 바둑판
                            Row(horizontalArrangement = Arrangement.Center) {
                                restaurant.tags.take(2).forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFE6E6F0))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "#${tag}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF333366)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text("주사위를 굴려보세요!", color = Color(0xFF888899))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(38.dp))
            // Fancy Dice 버튼 (Box로 커스텀 UI, Press 효과 등)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF4F61E9))
                    .padding(horizontal = 32.dp, vertical = 18.dp)
                    .shadow(2.dp, RoundedCornerShape(50))
                    .clickable { viewModel.recommend() },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎲", fontSize = 21.sp)
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(
                        "주사위 굴리기",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }
        }
    }
}
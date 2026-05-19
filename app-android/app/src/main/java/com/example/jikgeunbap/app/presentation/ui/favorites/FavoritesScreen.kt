package com.example.jikgeunbap.app.presentation.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jikgeunbap.app.presentation.theme.*
import com.example.jikgeunbap.domain.model.Restaurant

private fun categoryEmoji(category: String) = when (category) {
    "한식" -> "🍚"; "중식" -> "🥢"; "일식" -> "🍣"
    "양식" -> "🍝"; "분식" -> "🥟"; "카페", "카페/디저트" -> "☕"
    else   -> "🍽️"
}

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // 헤더
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(WarmOrange, WarmOrangeLight)))
                .padding(horizontal = 20.dp, vertical = 28.dp)
        ) {
            Column {
                Text(
                    text       = "찜한 맛집 ❤️",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 26.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "총 ${favorites.size}곳",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        if (favorites.isEmpty()) {
            EmptyFavorites(modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                contentPadding        = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement   = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.id }) { restaurant ->
                    FavoriteItem(
                        restaurant = restaurant,
                        onRemove   = { viewModel.removeFavorite(restaurant) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🤍", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text       = "아직 찜한 맛집이 없어요",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = WarmBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text      = "추천을 받고 ❤️를 눌러\n맛집을 저장해보세요!",
                fontSize  = 14.sp,
                color     = WarmBrownMid,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    restaurant: Restaurant,
    onRemove: () -> Unit
) {
    Card(
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors    = CardDefaults.cardColors(containerColor = WarmSurface)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 카테고리 아이콘
            Box(
                modifier         = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(WarmContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryEmoji(restaurant.category), fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = WarmBrown
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SmallBadge(text = restaurant.category, bgColor = WarmOrange, textColor = Color.White)
                    val dist = if (restaurant.distance < 1000) "${restaurant.distance}m"
                               else "${"%.1f".format(restaurant.distance / 1000.0)}km"
                    SmallBadge(text = dist, bgColor = WarmContainer, textColor = WarmBrownMid)
                }
                if (restaurant.rating > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text     = "⭐ ${"%.1f".format(restaurant.rating)}",
                        fontSize = 12.sp,
                        color    = WarmAmber
                    )
                }
            }

            // 삭제 버튼
            IconButton(onClick = onRemove) {
                Text("❤️", fontSize = 22.sp)
            }
        }
    }
}

@Composable
private fun SmallBadge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

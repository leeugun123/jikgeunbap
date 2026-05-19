package com.example.jikgeunbap.app.presentation.ui.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jikgeunbap.app.presentation.theme.*
import com.example.jikgeunbap.domain.model.Restaurant

private val CATEGORIES = listOf("전체", "한식", "중식", "일식", "양식", "분식", "카페")

private fun categoryEmoji(category: String) = when (category) {
    "한식" -> "🍚"
    "중식" -> "🥢"
    "일식" -> "🍣"
    "양식" -> "🍝"
    "분식" -> "🥟"
    "카페", "카페/디저트" -> "☕"
    else   -> "🍽️"
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onOpenWorkplace: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val restaurant       by viewModel.restaurant.collectAsState()
    val error            by viewModel.error.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isFavorite       by viewModel.isFavorite.collectAsState()
    val isLoading        by viewModel.isLoading.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream)
            .verticalScroll(rememberScrollState())
    ) {
        // ── 상단 헤더 ──────────────────────────────────────────
        TopHeader(onOpenWorkplace = onOpenWorkplace)

        // ── 카테고리 필터 칩 ───────────────────────────────────
        CategoryChips(
            categories       = CATEGORIES,
            selectedCategory = selectedCategory,
            onSelectCategory = { cat ->
                viewModel.selectCategory(if (cat == "전체") null else cat)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── 식당 카드 ──────────────────────────────────────────
        RestaurantCard(
            restaurant = restaurant,
            isFavorite = isFavorite,
            onToggleFavorite = { viewModel.toggleFavorite() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── 추천 버튼 ──────────────────────────────────────────
        RecommendButton(
            isLoading = isLoading,
            onClick   = { viewModel.recommend() },
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        // ── 에러 메시지 ────────────────────────────────────────
        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = "⚠️ $error",
                color     = WarmError,
                fontSize  = 13.sp,
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── 상단 헤더 ──────────────────────────────────────────────────────────────────
@Composable
private fun TopHeader(onOpenWorkplace: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(WarmOrange, WarmOrangeLight)
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Column {
            Text(
                text       = "직근밥 🍱",
                color      = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "오늘 점심, 여기 어때요?",
                color    = Color.White.copy(alpha = 0.88f),
                fontSize = 14.sp
            )
        }
        // 직장 위치 버튼
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White.copy(alpha = 0.22f))
                .clickable(onClick = onOpenWorkplace)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "📍 직장 설정",
                color      = Color.White,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── 카테고리 칩 ──────────────────────────────────────────────────────────────
@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String?,
    onSelectCategory: (String) -> Unit
) {
    LazyRow(
        contentPadding         = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement  = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { cat ->
            val isSelected = if (cat == "전체") selectedCategory == null
                             else selectedCategory == cat
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) WarmOrange else WarmContainer,
                label       = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else WarmBrownMid,
                label       = "chip_text"
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(bgColor)
                    .clickable { onSelectCategory(cat) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "${categoryEmoji(cat)} $cat",
                    color      = textColor,
                    fontSize   = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// ── 식당 카드 ─────────────────────────────────────────────────────────────────
@Composable
private fun RestaurantCard(
    restaurant: Restaurant?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape     = RoundedCornerShape(24.dp),
        modifier  = modifier.heightIn(min = 240.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors    = CardDefaults.cardColors(containerColor = WarmSurface)
    ) {
        if (restaurant != null) {
            RestaurantCardContent(
                restaurant       = restaurant,
                isFavorite       = isFavorite,
                onToggleFavorite = onToggleFavorite
            )
        } else {
            EmptyCardContent()
        }
    }
}

@Composable
private fun EmptyCardContent() {
    Box(
        modifier          = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment  = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎲", fontSize = 52.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text       = "아래 버튼을 눌러\n오늘의 점심을 추천받아요!",
                color      = WarmBrownMid,
                fontSize   = 15.sp,
                textAlign  = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun RestaurantCardContent(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit
) {
    val heartScale by animateFloatAsState(
        targetValue = if (isFavorite) 1.25f else 1f,
        animationSpec = spring(),
        label = "heart_scale"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment   = Alignment.CenterHorizontally
        ) {
            // 카테고리 아이콘
            Box(
                modifier         = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(WarmContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = categoryEmoji(restaurant.category),
                    fontSize = 38.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 식당 이름
            Text(
                text       = restaurant.name,
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 24.sp,
                color      = WarmBrown
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 카테고리 배지 + 거리 배지
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Badge(text = restaurant.category, bgColor = WarmOrange, textColor = Color.White)
                Badge(
                    text      = if (restaurant.distance < 1000) "${restaurant.distance}m"
                                else "${"%.1f".format(restaurant.distance / 1000.0)}km",
                    bgColor   = WarmContainer,
                    textColor = WarmBrownMid
                )
            }

            // 별점
            if (restaurant.rating > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                RatingRow(rating = restaurant.rating)
            }

            // 태그
            if (restaurant.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    restaurant.tags.take(3).forEach { tag ->
                        TagChip(tag = tag)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // 찜 버튼 (우상단)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isFavorite) Color(0xFFFFE8E8) else Color(0xFFF5F0EB))
                .clickable(onClick = onToggleFavorite)
                .scale(heartScale),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text     = if (isFavorite) "❤️" else "🤍",
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun Badge(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun RatingRow(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("⭐", fontSize = 14.sp)
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text       = "%.1f".format(rating),
            fontSize   = 14.sp,
            fontWeight = FontWeight.Bold,
            color      = WarmAmber
        )
    }
}

@Composable
private fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .padding(horizontal = 3.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(WarmContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = "#$tag", fontSize = 12.sp, color = WarmBrownMid)
    }
}

// ── 추천 버튼 ─────────────────────────────────────────────────────────────────
@Composable
private fun RecommendButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(WarmOrange, WarmOrangeLight)
                )
            )
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color    = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎲", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = "오늘의 점심 추천받기",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 18.sp
                )
            }
        }
    }
}

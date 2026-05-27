package com.example.jikgeunbap.app.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import com.example.jikgeunbap.app.presentation.theme.WarmAmber
import com.example.jikgeunbap.app.presentation.theme.WarmBrown
import com.example.jikgeunbap.app.presentation.theme.WarmBrownMid
import com.example.jikgeunbap.app.presentation.theme.WarmContainer
import com.example.jikgeunbap.app.presentation.theme.WarmCream
import com.example.jikgeunbap.app.presentation.theme.WarmError
import com.example.jikgeunbap.app.presentation.theme.WarmOrange
import com.example.jikgeunbap.app.presentation.theme.WarmOrangeLight
import com.example.jikgeunbap.app.presentation.theme.WarmSurface
import com.example.jikgeunbap.domain.model.Recommendation

private fun categoryEmoji(category: String?) = when (category) {
    "한식"             -> "🍚"
    "중식"             -> "🥢"
    "일식"             -> "🍣"
    "양식"             -> "🍝"
    "분식"             -> "🥟"
    "카페", "카페/디저트" -> "☕"
    else               -> "🍽️"
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onOpenWorkplace: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val recommendation by viewModel.recommendation.collectAsState()
    val error          by viewModel.error.collectAsState()
    val isLoading      by viewModel.isLoading.collectAsState()

    Column(
        modifier              = modifier
            .fillMaxSize()
            .background(WarmCream),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        TopHeader(onOpenWorkplace = onOpenWorkplace)

        Spacer(modifier = Modifier.height(28.dp))

        RecommendationCard(
            recommendation = recommendation,
            isLoading      = isLoading,
            modifier       = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (error != null) {
            Text(
                text      = "⚠️ $error",
                color     = WarmError,
                fontSize  = 13.sp,
                textAlign = TextAlign.Center,
                modifier  = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        RecommendButton(
            isLoading = isLoading,
            hasResult = recommendation != null,
            onClick   = { viewModel.recommend() },
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
    }
}

// ── 헤더 ──────────────────────────────────────────────────────────────────────
@Composable
private fun TopHeader(onOpenWorkplace: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(WarmOrange, WarmOrangeLight))
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
                text     = "AI가 골라주는 직장 근처 점심",
                color    = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
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
                text       = "⚙ 직장",
                color      = Color.White,
                fontSize   = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ── 추천 카드 ─────────────────────────────────────────────────────────────────
@Composable
private fun RecommendationCard(
    recommendation: Recommendation?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        shape     = RoundedCornerShape(24.dp),
        modifier  = modifier.heightIn(min = 280.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors    = CardDefaults.cardColors(containerColor = WarmSurface)
    ) {
        Box(
            modifier         = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading && recommendation == null -> LoadingContent()
                recommendation == null              -> EmptyContent()
                else                                -> ResultContent(recommendation)
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = WarmOrange, strokeWidth = 2.5.dp)
        Spacer(modifier = Modifier.height(14.dp))
        Text("🤖 AI가 고민 중...", color = WarmBrownMid, fontSize = 14.sp)
    }
}

@Composable
private fun EmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.padding(28.dp)
    ) {
        Text("🤖", fontSize = 54.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text       = "AI에게 오늘의 점심을 물어보세요",
            color      = WarmBrownMid,
            fontSize   = 15.sp,
            textAlign  = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ResultContent(rec: Recommendation) {
    Column(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(WarmContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(text = categoryEmoji(rec.restaurant.category), fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text       = rec.restaurant.name,
            fontWeight = FontWeight.ExtraBold,
            fontSize   = 24.sp,
            color      = WarmBrown,
            textAlign  = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text       = rec.restaurant.category,
                color      = WarmOrange,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = " · ", color = WarmBrownMid, fontSize = 13.sp)
            Text(
                text     = if (rec.restaurant.distance < 1000) "${rec.restaurant.distance}m"
                           else "${"%.1f".format(rec.restaurant.distance / 1000.0)}km",
                color    = WarmBrownMid,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // AI 추천 이유 (핵심)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(WarmAmber.copy(alpha = 0.12f))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(text = "🤖", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = rec.reason,
                    color      = WarmBrown,
                    fontSize   = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── 추천 버튼 ─────────────────────────────────────────────────────────────────
@Composable
private fun RecommendButton(
    isLoading: Boolean,
    hasResult: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(listOf(WarmOrange, WarmOrangeLight))
            )
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(PaddingValues(vertical = 18.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier    = Modifier.size(22.dp),
                color       = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (hasResult) "🔄" else "🤖", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text       = if (hasResult) "다른 추천 받기" else "AI에게 추천받기",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 17.sp
                )
            }
        }
    }
}

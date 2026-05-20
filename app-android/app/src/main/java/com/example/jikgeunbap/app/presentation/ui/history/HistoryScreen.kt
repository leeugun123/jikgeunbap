package com.example.jikgeunbap.app.presentation.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
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
import com.example.jikgeunbap.domain.model.HistoryEntry
import java.text.SimpleDateFormat
import java.util.*

private fun categoryEmoji(category: String) = when (category) {
    "한식" -> "🍚"; "중식" -> "🥢"; "일식" -> "🍣"
    "양식" -> "🍝"; "분식" -> "🥟"; "카페", "카페/디저트" -> "☕"
    else   -> "🍽️"
}

private fun dayLabel(millis: Long): String {
    val today     = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    val entryDay  = Calendar.getInstance().apply { timeInMillis = millis; set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }
    val diffDays  = ((today.timeInMillis - entryDay.timeInMillis) / 86_400_000).toInt()
    return when (diffDays) {
        0    -> "오늘"
        1    -> "어제"
        else -> "${diffDays}일 전"
    }
}

private fun timeLabel(millis: Long): String =
    SimpleDateFormat("HH:mm", Locale.KOREA).format(Date(millis))

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()

    // 탭 전환으로 돌아올 때마다 최신 데이터 반영
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.load()
        }
    }

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
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Text(
                    text       = "추천 히스토리 📋",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 26.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "총 ${history.size}번 추천받았어요",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
            if (history.isNotEmpty()) {
                TextButton(
                    onClick  = { viewModel.clearAll() },
                    modifier = Modifier.align(Alignment.TopEnd),
                    colors   = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Text("전체 삭제", fontSize = 12.sp)
                }
            }
        }

        if (history.isEmpty()) {
            EmptyHistory(modifier = Modifier.fillMaxSize())
        } else {
            // 날짜 그룹핑
            val grouped = history.groupBy { dayLabel(it.recommendedAt) }
            val dayOrder = listOf("오늘", "어제") + (2..365).map { "${it}일 전" }
            val sortedKeys = grouped.keys.sortedBy { key ->
                dayOrder.indexOf(key).let { if (it == -1) Int.MAX_VALUE else it }
            }

            LazyColumn(
                contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier            = Modifier.fillMaxSize()
            ) {
                sortedKeys.forEach { day ->
                    item {
                        Text(
                            text       = day,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp,
                            color      = WarmOrange,
                            modifier   = Modifier.padding(vertical = 6.dp)
                        )
                    }
                    items(grouped[day] ?: emptyList()) { entry ->
                        HistoryItem(entry = entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📋", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text       = "아직 추천 기록이 없어요",
                fontWeight = FontWeight.Bold,
                fontSize   = 18.sp,
                color      = WarmBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text      = "추천을 받으면 여기에\n기록이 쌓여요 🎲",
                fontSize  = 14.sp,
                color     = WarmBrownMid,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntry) {
    val restaurant = entry.restaurant
    Card(
        shape     = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors    = CardDefaults.cardColors(containerColor = WarmSurface)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘
            Box(
                modifier         = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(WarmContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(text = categoryEmoji(restaurant.category), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = WarmBrown
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "${restaurant.category}  ·  " +
                               if (restaurant.distance < 1000) "${restaurant.distance}m"
                               else "${"%.1f".format(restaurant.distance / 1000.0)}km",
                    fontSize = 12.sp,
                    color    = WarmBrownMid
                )
            }

            // 시각
            Text(
                text     = timeLabel(entry.recommendedAt),
                fontSize = 12.sp,
                color    = WarmBrownMid
            )
        }
    }
}

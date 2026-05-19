package com.example.jikgeunbap.app.presentation.ui.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.jikgeunbap.app.presentation.theme.*
import com.example.jikgeunbap.app.presentation.ui.common.KakaoMapPicker

@Composable
fun WorkplaceScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onSaved: (() -> Unit)? = null,
    viewModel: WorkplaceViewModel = hiltViewModel()
) {
    val lat         by viewModel.lat.collectAsState()
    val lng         by viewModel.lng.collectAsState()
    val placeName   by viewModel.placeName.collectAsState()
    val address     by viewModel.address.collectAsState()
    val radiusMeter by viewModel.radiusMeter.collectAsState()
    val message     by viewModel.message.collectAsState()
    val saved       by viewModel.saved.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(saved) {
        if (saved) {
            onSaved?.invoke()
            viewModel.consumeSavedEvent()
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
                if (onBack != null) {
                    TextButton(
                        onClick = onBack,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("← 뒤로", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text       = "📍 직장 위치 설정",
                    color      = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 24.sp
                )
                Text(
                    text     = "지도를 탭하거나 직접 입력하세요",
                    color    = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 장소명
            WarmTextField(
                value         = placeName,
                onValueChange = viewModel::onPlaceNameChange,
                label         = "장소명 (예: 본사, 강남지점)"
            )

            // 주소
            WarmTextField(
                value         = address,
                onValueChange = viewModel::onAddressChange,
                label         = "주소 (선택)"
            )

            // 위도/경도 Row
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                WarmTextField(
                    value         = lat,
                    onValueChange = viewModel::onLatChange,
                    label         = "위도 (lat)",
                    modifier      = Modifier.weight(1f)
                )
                WarmTextField(
                    value         = lng,
                    onValueChange = viewModel::onLngChange,
                    label         = "경도 (lng)",
                    modifier      = Modifier.weight(1f)
                )
            }

            // 반경 슬라이더
            RadiusSlider(
                radiusMeter = radiusMeter,
                onRadiusChange = viewModel::onRadiusChange
            )

            // 지도
            Text(
                text       = "🗺️ 지도에서 직장 위치를 탭해 선택",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = WarmBrown
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                KakaoMapPicker(
                    lat             = lat.toDoubleOrNull() ?: 37.5665,
                    lng             = lng.toDoubleOrNull() ?: 126.9780,
                    onPointSelected = viewModel::onMapPointSelected
                )
            }

            // 에러/성공 메시지
            if (message != null) {
                val isSuccess = message == "저장 완료"
                Text(
                    text  = if (isSuccess) "✅ $message" else "⚠️ $message",
                    color = if (isSuccess) WarmSuccess else WarmError,
                    fontSize = 13.sp
                )
            }

            // 저장 버튼
            Button(
                onClick  = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = WarmOrange)
            ) {
                Text(
                    text       = "저장하기",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 17.sp,
                    color      = Color.White
                )
            }
        }
    }
}

@Composable
private fun WarmTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        label         = { Text(label, fontSize = 13.sp) },
        modifier      = modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = WarmOrange,
            unfocusedBorderColor = WarmDivider,
            focusedLabelColor    = WarmOrange,
            cursorColor          = WarmOrange
        ),
        singleLine = true
    )
}

@Composable
private fun RadiusSlider(
    radiusMeter: Int,
    onRadiusChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WarmContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Text(
                text       = "📏 추천 반경",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = WarmBrown
            )
            Text(
                text       = "${radiusMeter}m",
                fontWeight = FontWeight.ExtraBold,
                fontSize   = 18.sp,
                color      = WarmOrange
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value         = radiusMeter.toFloat(),
            onValueChange = { onRadiusChange(it.toInt()) },
            valueRange    = 200f..2000f,
            steps         = 17,   // 200~2000, 100m 단위 → 18 구간 → 17 steps
            colors        = SliderDefaults.colors(
                thumbColor          = WarmOrange,
                activeTrackColor    = WarmOrange,
                inactiveTrackColor  = WarmDivider
            )
        )
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("200m", fontSize = 11.sp, color = WarmBrownMid)
            Text("2,000m", fontSize = 11.sp, color = WarmBrownMid)
        }
    }
}

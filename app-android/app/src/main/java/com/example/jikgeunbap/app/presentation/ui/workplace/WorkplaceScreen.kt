package com.example.jikgeunbap.app.presentation.ui.workplace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.jikgeunbap.domain.model.KakaoPlace

@Composable
fun WorkplaceScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onSaved: (() -> Unit)? = null,
    viewModel: WorkplaceViewModel = hiltViewModel()
) {
    val lat           by viewModel.lat.collectAsState()
    val lng           by viewModel.lng.collectAsState()
    val placeName     by viewModel.placeName.collectAsState()
    val address       by viewModel.address.collectAsState()
    val message       by viewModel.message.collectAsState()
    val saved         by viewModel.saved.collectAsState()
    val searchQuery   by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching   by viewModel.isSearching.collectAsState()

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
                    text     = "검색하거나 지도에서 위치를 선택하세요",
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
            // ── 키워드 검색 ────────────────────────────────────────────────
            KeywordSearchSection(
                query         = searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch      = viewModel::searchPlace,
                isSearching   = isSearching,
                results       = searchResults,
                onSelectPlace = viewModel::selectPlace
            )

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

// ── 키워드 검색 섹션 ──────────────────────────────────────────────────────────
@Composable
private fun KeywordSearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    isSearching: Boolean,
    results: List<KakaoPlace>,
    onSelectPlace: (KakaoPlace) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WarmContainer)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text       = "🔍 직장 위치 검색",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp,
            color      = WarmBrown
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment     = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value         = query,
                onValueChange = onQueryChange,
                placeholder   = { Text("회사명 또는 주소 입력", fontSize = 13.sp) },
                modifier      = Modifier.weight(1f),
                shape         = RoundedCornerShape(12.dp),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = WarmOrange,
                    unfocusedBorderColor = WarmDivider,
                    cursorColor          = WarmOrange
                ),
                singleLine = true
            )
            Button(
                onClick  = onSearch,
                enabled  = !isSearching && query.isNotBlank(),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = WarmOrange),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
            ) {
                if (isSearching) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(18.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("검색", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        if (results.isNotEmpty()) {
            LazyColumn(
                modifier              = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 240.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(WarmSurface),
                verticalArrangement   = Arrangement.spacedBy(0.dp)
            ) {
                items(results) { place ->
                    PlaceResultItem(place = place, onClick = { onSelectPlace(place) })
                    Divider(color = WarmDivider, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
private fun PlaceResultItem(place: KakaoPlace, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text       = place.placeName,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp,
            color      = WarmBrown
        )
        val displayAddr = place.roadAddressName.ifBlank { place.addressName }
        if (displayAddr.isNotBlank()) {
            Text(
                text     = displayAddr,
                fontSize = 12.sp,
                color    = WarmBrownMid
            )
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


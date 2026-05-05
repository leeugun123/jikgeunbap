package com.example.jikgeunbap.app.presentation.ui.workplace

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

@Composable
fun WorkplaceScreen(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onSaved: (() -> Unit)? = null,
    viewModel: WorkplaceViewModel = hiltViewModel()
) {
    val lat = viewModel.lat.collectAsState().value
    val lng = viewModel.lng.collectAsState().value
    val placeName = viewModel.placeName.collectAsState().value
    val address = viewModel.address.collectAsState().value
    val radiusMeter = viewModel.radiusMeter.collectAsState().value
    val message = viewModel.message.collectAsState().value
    val saved = viewModel.saved.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    LaunchedEffect(saved) {
        if (saved) {
            onSaved?.invoke()
            viewModel.consumeSavedEvent()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("직장 위치 설정")
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = placeName,
            onValueChange = viewModel::onPlaceNameChange,
            label = { Text("장소명(예: 본사/지점)") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = address,
            onValueChange = viewModel::onAddressChange,
            label = { Text("주소(선택)") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lat,
            onValueChange = viewModel::onLatChange,
            label = { Text("위도(lat)") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = lng,
            onValueChange = viewModel::onLngChange,
            label = { Text("경도(lng)") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = radiusMeter,
            onValueChange = viewModel::onRadiusChange,
            label = { Text("추천 반경(m)") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text("카카오맵에서 직장 위치를 탭해 선택")
        Spacer(modifier = Modifier.height(8.dp))
        KakaoMapPicker(
            lat = lat.toDoubleOrNull() ?: 37.5665,
            lng = lng.toDoubleOrNull() ?: 126.9780,
            onPointSelected = viewModel::onMapPointSelected
        )
        if (message != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = Color(0xFFCC3344))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = viewModel::save) {
            Text("저장")
        }
        if (onBack != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onBack) {
                Text("뒤로")
            }
        }
    }
}

@SuppressLint("ViewConstructor")
@Composable
private fun KakaoMapPicker(
    lat: Double,
    lng: Double,
    onPointSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember { MapView(context) }

    DisposableEffect(mapView, lifecycleOwner, lat, lng) {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() = Unit
                override fun onMapError(error: Exception?) = Unit
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: com.kakao.vectormap.KakaoMap) {
                    kakaoMap.setOnMapClickListener { _, position, _, _ ->
                        onPointSelected(position.latitude, position.longitude)
                    }
                }

                override fun getPosition(): LatLng = LatLng.from(lat, lng)
            }
        )

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.pause()
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { mapView }
    )
}


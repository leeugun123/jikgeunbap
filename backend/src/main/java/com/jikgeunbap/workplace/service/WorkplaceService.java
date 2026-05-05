package com.jikgeunbap.workplace.service;

import com.jikgeunbap.workplace.entity.WorkplaceSetting;
import com.jikgeunbap.workplace.dto.WorkplaceRequest;
import com.jikgeunbap.workplace.dto.WorkplaceResponse;
import com.jikgeunbap.workplace.repository.WorkplaceSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkplaceService {

    private static final long SINGLETON_ID = 1L;

    private final WorkplaceSettingRepository repository;

    public WorkplaceResponse get() {
        WorkplaceSetting setting = repository.findById(SINGLETON_ID)
                .orElseGet(() -> repository.save(WorkplaceSetting.builder()
                        .id(SINGLETON_ID)
                        .latitude(37.5665)
                        .longitude(126.9780)
                        .placeName("시청역 인근")
                        .address("서울 중구 세종대로")
                        .radiusMeter(500)
                        .mapProvider("manual")
                        .build()));

        return toResponse(setting);
    }

    public WorkplaceResponse set(WorkplaceRequest request) {
        WorkplaceSetting saved = repository.save(WorkplaceSetting.builder()
                .id(SINGLETON_ID)
                .latitude(request.lat())
                .longitude(request.lng())
                .placeName(defaultText(request.placeName(), "내 직장"))
                .address(defaultText(request.address(), "주소 미입력"))
                .radiusMeter(defaultRadius(request.radiusMeter()))
                .mapProvider(defaultText(request.mapProvider(), "manual"))
                .build());

        return toResponse(saved);
    }

    private WorkplaceResponse toResponse(WorkplaceSetting setting) {
        return new WorkplaceResponse(
                setting.getLatitude(),
                setting.getLongitude(),
                defaultText(setting.getPlaceName(), "내 직장"),
                defaultText(setting.getAddress(), "주소 미입력"),
                defaultRadius(setting.getRadiusMeter()),
                defaultText(setting.getMapProvider(), "manual")
        );
    }

    private String defaultText(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback;
        return value;
    }

    private int defaultRadius(Integer radiusMeter) {
        if (radiusMeter == null || radiusMeter <= 0) return 500;
        return radiusMeter;
    }
}


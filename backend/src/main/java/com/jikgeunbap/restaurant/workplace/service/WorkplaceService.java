package com.jikgeunbap.restaurant.workplace.service;

import com.jikgeunbap.restaurant.workplace.entity.WorkplaceSetting;
import com.jikgeunbap.restaurant.workplace.dto.WorkplaceRequest;
import com.jikgeunbap.restaurant.workplace.dto.WorkplaceResponse;
import com.jikgeunbap.restaurant.workplace.repository.WorkplaceSettingRepository;
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
                        .build()));

        return new WorkplaceResponse(setting.getLatitude(), setting.getLongitude());
    }

    public WorkplaceResponse set(WorkplaceRequest request) {
        WorkplaceSetting setting = repository.findById(SINGLETON_ID)
                .orElseGet(() -> WorkplaceSetting.builder().id(SINGLETON_ID).build());

        WorkplaceSetting saved = repository.save(WorkplaceSetting.builder()
                .id(SINGLETON_ID)
                .latitude(request.lat())
                .longitude(request.lng())
                .build());

        return new WorkplaceResponse(saved.getLatitude(), saved.getLongitude());
    }
}


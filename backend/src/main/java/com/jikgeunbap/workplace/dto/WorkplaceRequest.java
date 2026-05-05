package com.jikgeunbap.workplace.dto;

public record WorkplaceRequest(
        double lat,
        double lng,
        String placeName,
        String address,
        Integer radiusMeter,
        String mapProvider
) {
}


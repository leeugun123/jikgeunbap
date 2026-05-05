package com.jikgeunbap.workplace.dto;

public record WorkplaceResponse(
        double lat,
        double lng,
        String placeName,
        String address,
        int radiusMeter,
        String mapProvider
) {
}


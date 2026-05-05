package com.jikgeunbap.workplace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workplace_setting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkplaceSetting {
    @Id
    private Long id; // 싱글톤(항상 1)

    private Double latitude;
    private Double longitude;

    private String placeName;
    private String address;
    private Integer radiusMeter;
    private String mapProvider;
}


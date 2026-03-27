package com.jikgeunbap.workplace.repository;

import com.jikgeunbap.workplace.entity.WorkplaceSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkplaceSettingRepository extends JpaRepository<WorkplaceSetting, Long> {
}


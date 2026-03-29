package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import javax.inject.Inject

class GetWorkplaceUseCase @Inject constructor(
    private val workplaceRepository: WorkplaceRepository
) {
    suspend operator fun invoke(): Workplace = workplaceRepository.getWorkplace()
}

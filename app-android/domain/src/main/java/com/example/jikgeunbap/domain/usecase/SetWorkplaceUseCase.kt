package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import javax.inject.Inject

class SetWorkplaceUseCase @Inject constructor(
    private val workplaceRepository: WorkplaceRepository
) {
    suspend operator fun invoke(workplace: Workplace): Workplace =
        workplaceRepository.setWorkplace(workplace)
}

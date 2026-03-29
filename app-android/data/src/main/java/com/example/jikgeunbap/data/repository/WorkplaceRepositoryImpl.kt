package com.example.jikgeunbap.data.repository

import com.example.jikgeunbap.data.source.remote.RestaurantApiService
import com.example.jikgeunbap.data.source.remote.WorkplaceDto
import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import javax.inject.Inject

class WorkplaceRepositoryImpl @Inject constructor(
    private val apiService: RestaurantApiService
) : WorkplaceRepository {

    override suspend fun getWorkplace(): Workplace {
        val dto = apiService.getWorkplace()
        return dto.toDomain()
    }

    override suspend fun setWorkplace(workplace: Workplace): Workplace {
        val dto = apiService.setWorkplace(WorkplaceDto(lat = workplace.lat, lng = workplace.lng))
        return dto.toDomain()
    }

    private fun WorkplaceDto.toDomain() = Workplace(lat = lat, lng = lng)
}

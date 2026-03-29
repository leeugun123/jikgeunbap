package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.Workplace

interface WorkplaceRepository {
    suspend fun getWorkplace(): Workplace
    suspend fun setWorkplace(workplace: Workplace): Workplace
}

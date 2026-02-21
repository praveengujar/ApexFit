package com.zyva.core.domain.usecase

import com.zyva.core.data.entity.DailyMetricEntity
import com.zyva.core.data.repository.DailyMetricRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveDailyMetricUseCase @Inject constructor(
    private val dailyMetricRepo: DailyMetricRepository,
) {
    fun observeToday(): Flow<DailyMetricEntity?> {
        val todayMillis = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return dailyMetricRepo.observeByDate(todayMillis)
    }

    fun observeForDate(date: LocalDate): Flow<DailyMetricEntity?> {
        val millis = date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return dailyMetricRepo.observeByDate(millis)
    }

    fun observeRecent(days: Int = 7): Flow<List<DailyMetricEntity>> {
        return dailyMetricRepo.observeRecent(days)
    }
}

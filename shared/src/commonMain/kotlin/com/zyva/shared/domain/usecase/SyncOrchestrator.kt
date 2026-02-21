package com.zyva.shared.domain.usecase

import com.zyva.shared.domain.repository.DailyMetricRepository
import com.zyva.shared.model.DailyMetric
import com.zyva.shared.network.ZyvaApiClient
import com.zyva.shared.network.dto.MetricsSyncRequestDto
import com.zyva.shared.network.dto.WorkoutSyncDataDto

class SyncOrchestrator(
    private val apiClient: ZyvaApiClient,
    private val dailyMetricRepo: DailyMetricRepository,
) {
    suspend fun syncPendingToCloud() {
        val startDate = 0L // Fetch all unsynced
        val endDate = Long.MAX_VALUE
        val allMetrics = dailyMetricRepo.getRange(startDate, endDate)
        val unsynced = allMetrics.filter { it.isComputed && !it.syncedToCloud }

        for (metric in unsynced) {
            syncMetric(metric)
        }
    }

    private suspend fun syncMetric(metric: DailyMetric) {
        val workoutDtos = metric.workouts.map { workout ->
            WorkoutSyncDataDto(
                workoutType = workout.workoutType,
                workoutName = workout.workoutName,
                startDate = workout.startDate.toString(),
                endDate = workout.endDate.toString(),
                strainScore = workout.strainScore,
                averageHeartRate = workout.averageHeartRate,
                maxHeartRate = workout.maxHeartRate,
                activeCalories = workout.activeCalories,
                zone1Minutes = workout.zone1Minutes,
                zone2Minutes = workout.zone2Minutes,
                zone3Minutes = workout.zone3Minutes,
                zone4Minutes = workout.zone4Minutes,
                zone5Minutes = workout.zone5Minutes,
            )
        }

        val request = MetricsSyncRequestDto(
            date = metric.date.toString(),
            recoveryScore = metric.recoveryScore,
            recoveryZone = metric.recoveryZone?.name,
            strainScore = metric.strainScore,
            sleepPerformance = metric.sleepPerformance,
            hrvRmssd = metric.hrvRMSSD,
            hrvSdnn = metric.hrvSDNN,
            restingHeartRate = metric.restingHeartRate,
            respiratoryRate = metric.respiratoryRate,
            spo2 = metric.spo2,
            steps = metric.steps,
            activeCalories = metric.activeCalories,
            vo2Max = metric.vo2Max,
            sleepDurationHours = metric.sleepDurationHours,
            sleepNeedHours = metric.sleepNeedHours,
            workouts = workoutDtos,
        )

        apiClient.syncMetrics(request)

        // Mark as synced
        dailyMetricRepo.insertOrUpdate(metric.copy(syncedToCloud = true))
    }

    suspend fun fetchFromCloud(fromMillis: Long, toMillis: Long) {
        val response = apiClient.getDailyMetrics(
            from = fromMillis.toString(),
            to = toMillis.toString(),
        )

        for (serverMetric in response.metrics) {
            val localMetric = dailyMetricRepo.getByDate(serverMetric.date.toLongOrNull() ?: continue)

            // Server wins if local doesn't exist or local isn't computed
            if (localMetric == null || !localMetric.isComputed) {
                val imported = DailyMetric(
                    date = serverMetric.date.toLongOrNull() ?: continue,
                    recoveryScore = serverMetric.recoveryScore,
                    strainScore = serverMetric.strainScore,
                    sleepPerformance = serverMetric.sleepPerformance,
                    hrvRMSSD = serverMetric.hrvRmssd,
                    restingHeartRate = serverMetric.restingHeartRate,
                    steps = serverMetric.steps,
                    activeCalories = serverMetric.activeCalories,
                    isComputed = true,
                    syncedToCloud = true,
                )
                dailyMetricRepo.insertOrUpdate(imported)
            }
        }
    }
}

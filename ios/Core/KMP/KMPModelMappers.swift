import Foundation
import SwiftData
import ZyvaShared

// Typealiases to avoid naming collisions between SwiftData models and KMP models.
// In Swift, the KMP types are accessed as ZyvaShared.DailyMetric, etc.
// Use these aliases for clarity in mapper code.
typealias SharedDailyMetric = ZyvaShared.DailyMetric
typealias SharedWorkoutRecord = ZyvaShared.WorkoutRecord
typealias SharedSleepSession = ZyvaShared.SleepSession
typealias SharedUserProfile = ZyvaShared.UserProfile
typealias SharedBaselineMetric = ZyvaShared.BaselineMetric

// MARK: - DailyMetric Mappers (SwiftData <-> KMP)

extension DailyMetric {
    /// Convert SwiftData DailyMetric to KMP shared DailyMetric.
    func toShared() -> SharedDailyMetric {
        let sharedWorkouts = workouts.map { $0.toShared() }
        let sharedSleepSessions = sleepSessions.map { $0.toShared() }

        return SharedDailyMetric(
            id: id.uuidString,
            date: date.epochMillis,
            recoveryScore: recoveryScore.map { KotlinDouble(value: $0) },
            recoveryZone: recoveryZone?.toKMP,
            strainScore: strainScore,
            sleepPerformance: sleepPerformance.map { KotlinDouble(value: $0) },
            hrvRMSSD: hrvRMSSD.map { KotlinDouble(value: $0) },
            hrvSDNN: hrvSDNN.map { KotlinDouble(value: $0) },
            restingHeartRate: restingHeartRate.map { KotlinDouble(value: $0) },
            respiratoryRate: respiratoryRate.map { KotlinDouble(value: $0) },
            spo2: spo2.map { KotlinDouble(value: $0) },
            skinTemperature: skinTemperature.map { KotlinDouble(value: $0) },
            steps: Int32(steps),
            activeCalories: activeCalories,
            vo2Max: vo2Max.map { KotlinDouble(value: $0) },
            peakStrain: peakStrain,
            workoutCount: Int32(workoutCount),
            sleepDurationHours: sleepDurationHours.map { KotlinDouble(value: $0) },
            sleepNeedHours: sleepNeedHours.map { KotlinDouble(value: $0) },
            sleepDebtHours: sleepDebtHours.map { KotlinDouble(value: $0) },
            stressAverage: stressAverage.map { KotlinDouble(value: $0) },
            sleepScore: sleepScore.map { KotlinDouble(value: $0) },
            sleepConsistency: sleepConsistency.map { KotlinDouble(value: $0) },
            sleepEfficiencyPct: sleepEfficiencyPct.map { KotlinDouble(value: $0) },
            restorativeSleepPct: restorativeSleepPct.map { KotlinDouble(value: $0) },
            deepSleepPct: deepSleepPct.map { KotlinDouble(value: $0) },
            remSleepPct: remSleepPct.map { KotlinDouble(value: $0) },
            isComputed: isComputed,
            computedAt: computedAt.map { KotlinLong(value: $0.epochMillis) },
            syncedToCloud: syncedToCloud,
            createdAt: createdAt.epochMillis,
            userProfileId: userProfile?.id.uuidString,
            workouts: sharedWorkouts,
            sleepSessions: sharedSleepSessions
        )
    }

    /// Update this SwiftData DailyMetric from a KMP shared DailyMetric.
    func update(from shared: SharedDailyMetric) {
        recoveryScore = shared.recoveryScore?.doubleValue
        recoveryZone = shared.recoveryZone?.toiOS
        strainScore = shared.strainScore
        sleepPerformance = shared.sleepPerformance?.doubleValue
        hrvRMSSD = shared.hrvRMSSD?.doubleValue
        hrvSDNN = shared.hrvSDNN?.doubleValue
        restingHeartRate = shared.restingHeartRate?.doubleValue
        respiratoryRate = shared.respiratoryRate?.doubleValue
        spo2 = shared.spo2?.doubleValue
        skinTemperature = shared.skinTemperature?.doubleValue
        steps = Int(shared.steps)
        activeCalories = shared.activeCalories
        vo2Max = shared.vo2Max?.doubleValue
        peakStrain = shared.peakStrain
        workoutCount = Int(shared.workoutCount)
        sleepDurationHours = shared.sleepDurationHours?.doubleValue
        sleepNeedHours = shared.sleepNeedHours?.doubleValue
        sleepDebtHours = shared.sleepDebtHours?.doubleValue
        stressAverage = shared.stressAverage?.doubleValue
        sleepScore = shared.sleepScore?.doubleValue
        sleepConsistency = shared.sleepConsistency?.doubleValue
        sleepEfficiencyPct = shared.sleepEfficiencyPct?.doubleValue
        restorativeSleepPct = shared.restorativeSleepPct?.doubleValue
        deepSleepPct = shared.deepSleepPct?.doubleValue
        remSleepPct = shared.remSleepPct?.doubleValue
        isComputed = shared.isComputed
        if let ct = shared.computedAt {
            computedAt = Date(epochMillis: ct.int64Value)
        }
        syncedToCloud = shared.syncedToCloud
    }
}

// MARK: - WorkoutRecord Mappers

extension WorkoutRecord {
    func toShared() -> SharedWorkoutRecord {
        return SharedWorkoutRecord(
            id: id.uuidString,
            workoutType: workoutType,
            workoutName: workoutName,
            startDate: startDate.epochMillis,
            endDate: endDate.epochMillis,
            durationMinutes: durationMinutes,
            strainScore: strainScore,
            averageHeartRate: averageHeartRate.map { KotlinDouble(value: $0) },
            maxHeartRate: maxHeartRate.map { KotlinDouble(value: $0) },
            activeCalories: activeCalories,
            distanceMeters: distanceMeters.map { KotlinDouble(value: $0) },
            zone1Minutes: zone1Minutes,
            zone2Minutes: zone2Minutes,
            zone3Minutes: zone3Minutes,
            zone4Minutes: zone4Minutes,
            zone5Minutes: zone5Minutes,
            muscularLoad: muscularLoad.map { KotlinDouble(value: $0) },
            isStrengthWorkout: isStrengthWorkout,
            healthConnectUUID: healthKitWorkoutUUID,
            createdAt: createdAt.epochMillis,
            dailyMetricId: nil
        )
    }
}

// MARK: - SleepSession Mappers

extension SleepSession {
    func toShared() -> SharedSleepSession {
        let sharedStages = stages.map { $0.toShared() }
        return SharedSleepSession(
            id: id.uuidString,
            startDate: startDate.epochMillis,
            endDate: endDate.epochMillis,
            isMainSleep: isMainSleep,
            isNap: isNap,
            totalSleepMinutes: totalSleepMinutes,
            timeInBedMinutes: timeInBedMinutes,
            lightSleepMinutes: lightSleepMinutes,
            deepSleepMinutes: deepSleepMinutes,
            remSleepMinutes: remSleepMinutes,
            awakeMinutes: awakeMinutes,
            awakenings: Int32(awakenings),
            sleepOnsetLatencyMinutes: sleepOnsetLatencyMinutes.map { KotlinDouble(value: $0) },
            sleepEfficiency: sleepEfficiency,
            sleepPerformance: sleepPerformance.map { KotlinDouble(value: $0) },
            sleepNeedHours: sleepNeedHours.map { KotlinDouble(value: $0) },
            createdAt: createdAt.epochMillis,
            dailyMetricId: nil,
            stages: sharedStages
        )
    }
}

// MARK: - SleepStage Mappers

extension SleepStage {
    func toShared() -> ZyvaShared.SleepStage {
        return ZyvaShared.SleepStage(
            id: id.uuidString,
            stageType: stageType.toKMP,
            startDate: startDate.epochMillis,
            endDate: endDate.epochMillis,
            durationMinutes: durationMinutes,
            sleepSessionId: nil
        )
    }
}

// MARK: - UserProfile Mappers

extension UserProfile {
    func toShared() -> SharedUserProfile {
        return SharedUserProfile(
            id: id.uuidString,
            firebaseUID: firebaseUID,
            displayName: displayName,
            email: email,
            dateOfBirthMillis: dateOfBirth.map { KotlinLong(value: $0.epochMillis) },
            biologicalSex: biologicalSex.toKMP,
            heightCM: heightCM.map { KotlinDouble(value: $0) },
            weightKG: weightKG.map { KotlinDouble(value: $0) },
            maxHeartRate: maxHeartRate.map { KotlinInt(value: Int32($0)) },
            maxHeartRateSource: maxHeartRateSource.toKMP,
            sleepBaselineHours: sleepBaselineHours,
            preferredUnits: preferredUnits.toKMP,
            selectedJournalBehaviorIDs: selectedJournalBehaviorIDs,
            hasCompletedOnboarding: hasCompletedOnboarding,
            createdAt: createdAt.epochMillis,
            updatedAt: updatedAt.epochMillis,
            deviceToken: deviceToken,
            lastSyncedAt: lastSyncedAt.map { KotlinLong(value: $0.epochMillis) },
            wearableDevice: wearableDevice
        )
    }
}

// MARK: - BaselineMetric Mappers

extension BaselineMetric {
    func toShared() -> SharedBaselineMetric {
        return SharedBaselineMetric(
            id: id.uuidString,
            metricType: metricType.toKMP,
            mean: mean,
            standardDeviation: standardDeviation,
            sampleCount: Int32(sampleCount),
            windowStartDate: windowStartDate.epochMillis,
            windowEndDate: windowEndDate.epochMillis,
            updatedAt: updatedAt.epochMillis
        )
    }
}

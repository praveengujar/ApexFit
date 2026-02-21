package com.zyva.core.data.repository

import com.zyva.core.data.dao.NotificationPreferenceDao
import com.zyva.core.data.entity.NotificationPreferenceEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPreferenceRepository @Inject constructor(
    private val dao: NotificationPreferenceDao,
) {
    fun observeAll(): Flow<List<NotificationPreferenceEntity>> = dao.observeAll()

    suspend fun getByType(type: String): NotificationPreferenceEntity? = dao.getByType(type)

    suspend fun insert(preference: NotificationPreferenceEntity) = dao.insert(preference)

    suspend fun insertAll(preferences: List<NotificationPreferenceEntity>) = dao.insertAll(preferences)

    suspend fun setEnabled(type: String, enabled: Boolean) = dao.setEnabled(type, enabled)
}

package com.apexfit.core.data.repository

import com.apexfit.core.data.dao.JournalDao
import com.apexfit.core.data.entity.JournalEntryEntity
import com.apexfit.core.data.entity.JournalResponseEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(
    private val dao: JournalDao,
) {
    fun observeEntryByDate(date: Long): Flow<JournalEntryEntity?> =
        dao.observeEntryByDate(date)

    suspend fun getEntryByDate(date: Long): JournalEntryEntity? =
        dao.getEntryByDate(date)

    fun observeRecentEntries(limit: Int = 30): Flow<List<JournalEntryEntity>> =
        dao.observeRecentEntries(limit)

    suspend fun getCompletedEntries(): List<JournalEntryEntity> =
        dao.getCompletedEntries()

    fun observeResponses(entryId: String): Flow<List<JournalResponseEntity>> =
        dao.observeResponses(entryId)

    suspend fun getResponses(entryId: String): List<JournalResponseEntity> =
        dao.getResponses(entryId)

    suspend fun getResponsesByBehavior(behaviorId: String): List<JournalResponseEntity> =
        dao.getResponsesByBehavior(behaviorId)

    suspend fun insertEntryWithResponses(entry: JournalEntryEntity, responses: List<JournalResponseEntity>) =
        dao.insertEntryWithResponses(entry, responses)

    suspend fun updateEntry(entry: JournalEntryEntity) =
        dao.updateEntry(entry)

    suspend fun getCompletedCount(): Int =
        dao.getCompletedCount()
}

package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.apexfit.core.data.entity.JournalEntryEntity
import com.apexfit.core.data.entity.JournalResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun observeEntryByDate(date: Long): Flow<JournalEntryEntity?>

    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryByDate(date: Long): JournalEntryEntity?

    @Query("SELECT * FROM journal_entries ORDER BY date DESC LIMIT :limit")
    fun observeRecentEntries(limit: Int = 30): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE isComplete = 1 ORDER BY date DESC")
    suspend fun getCompletedEntries(): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_responses WHERE journalEntryId = :entryId")
    fun observeResponses(entryId: String): Flow<List<JournalResponseEntity>>

    @Query("SELECT * FROM journal_responses WHERE journalEntryId = :entryId")
    suspend fun getResponses(entryId: String): List<JournalResponseEntity>

    @Query("SELECT * FROM journal_responses WHERE behaviorID = :behaviorId")
    suspend fun getResponsesByBehavior(behaviorId: String): List<JournalResponseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntryEntity)

    @Update
    suspend fun updateEntry(entry: JournalEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResponses(responses: List<JournalResponseEntity>)

    @Transaction
    suspend fun insertEntryWithResponses(entry: JournalEntryEntity, responses: List<JournalResponseEntity>) {
        insertEntry(entry)
        insertResponses(responses)
    }

    @Query("SELECT COUNT(*) FROM journal_entries WHERE isComplete = 1")
    suspend fun getCompletedCount(): Int
}

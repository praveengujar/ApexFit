package com.apexfit.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userProfileId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("userProfileId"),
        Index("date"),
    ],
)
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val userProfileId: String,
    val date: Long,
    val completedAt: Long? = null,
    val isComplete: Boolean = false,
    val streakDays: Int = 0,
)

package com.apexfit.core.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_responses",
    foreignKeys = [
        ForeignKey(
            entity = JournalEntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["journalEntryId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("journalEntryId")],
)
data class JournalResponseEntity(
    @PrimaryKey val id: String,
    val journalEntryId: String,
    val behaviorID: String,
    val behaviorName: String,
    val category: String,
    val responseType: String, // TOGGLE, NUMERIC, SCALE
    val toggleValue: Boolean? = null,
    val numericValue: Double? = null,
    val scaleValue: Int? = null,
)

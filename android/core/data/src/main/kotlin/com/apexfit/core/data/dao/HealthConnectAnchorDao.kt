package com.apexfit.core.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.apexfit.core.data.entity.HealthConnectAnchorEntity

@Dao
interface HealthConnectAnchorDao {
    @Query("SELECT * FROM health_connect_anchors WHERE dataTypeIdentifier = :identifier LIMIT 1")
    suspend fun getByIdentifier(identifier: String): HealthConnectAnchorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(anchor: HealthConnectAnchorEntity)

    @Query("DELETE FROM health_connect_anchors")
    suspend fun deleteAll()
}

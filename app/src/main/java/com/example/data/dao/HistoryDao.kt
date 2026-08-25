package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
  @Query("SELECT * FROM history ORDER BY visitTime DESC LIMIT 200")
  fun getAllHistory(): Flow<List<HistoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertHistory(history: HistoryEntity): Long

  @Query("DELETE FROM history WHERE id = :id")
  suspend fun deleteById(id: Long)

  @Query("DELETE FROM history")
  suspend fun clearAllHistory()
}

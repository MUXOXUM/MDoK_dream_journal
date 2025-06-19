package com.example.dreamapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY date DESC")
    fun getAllDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams WHERE id = :id")
    suspend fun getDreamById(id: Long): DreamEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDream(dream: DreamEntity): Long

    @Update
    suspend fun updateDream(dream: DreamEntity)

    @Delete
    suspend fun deleteDream(dream: DreamEntity)

    @Query("DELETE FROM dreams WHERE id = :id")
    suspend fun deleteDreamById(id: Long)

    @Query("SELECT COUNT(*) FROM dreams")
    suspend fun getDreamCount(): Int

    @Query("SELECT COUNT(*) FROM dreams WHERE isLucid = 1")
    suspend fun getLucidDreamCount(): Int

    @Query("SELECT tags FROM dreams WHERE tags IS NOT NULL AND tags != ''")
    suspend fun getAllTags(): List<String>
} 
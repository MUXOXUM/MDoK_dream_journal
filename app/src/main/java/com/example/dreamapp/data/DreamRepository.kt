package com.example.dreamapp.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DreamRepository(private val dreamDao: DreamDao) {
    
    val allDreams: Flow<List<Dream>> = dreamDao.getAllDreams().map { entities ->
        entities.map { it.toDream() }
    }

    suspend fun getDreamById(id: Long): Dream? {
        return dreamDao.getDreamById(id)?.toDream()
    }

    suspend fun insertDream(dream: Dream): Long {
        return dreamDao.insertDream(DreamEntity.fromDream(dream))
    }

    suspend fun updateDream(dream: Dream) {
        dreamDao.updateDream(DreamEntity.fromDream(dream))
    }

    suspend fun deleteDream(dream: Dream) {
        dreamDao.deleteDream(DreamEntity.fromDream(dream))
    }

    suspend fun deleteDreamById(id: Long) {
        dreamDao.deleteDreamById(id)
    }

    suspend fun getDreamCount(): Int {
        return dreamDao.getDreamCount()
    }

    suspend fun getLucidDreamCount(): Int {
        return dreamDao.getLucidDreamCount()
    }

    suspend fun getAllTags(): List<String> {
        return dreamDao.getAllTags().flatMap { it.split(",").filter { tag -> tag.isNotBlank() } }
    }
} 
package com.example.dreamapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class FirestoreRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    private fun getDreamsCollection() = firestore
        .collection("users")
        .document(getCurrentUserId() ?: "")
        .collection("dreams")

    fun getAllDreams(): Flow<List<Dream>> = flow {
        try {
            val userId = getCurrentUserId()
            if (userId == null) {
                emit(emptyList())
                return@flow
            }

            val snapshot = getDreamsCollection()
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            val dreams = snapshot.documents.mapNotNull { document ->
                document.toObject(DreamFirestore::class.java)?.toDream()
            }
            emit(dreams)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun addDream(dream: Dream): Result<String> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val dreamFirestore = DreamFirestore.fromDream(dream)
            val documentReference = getDreamsCollection().add(dreamFirestore).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDream(dream: Dream): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            val dreamFirestore = DreamFirestore.fromDream(dream)
            getDreamsCollection().document(dream.id.toString()).set(dreamFirestore).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDream(dreamId: Long): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            getDreamsCollection().document(dreamId.toString()).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncDreamsToCloud(localDreams: List<Dream>): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("User not authenticated"))
            }

            // Получаем существующие сны из облака
            val cloudSnapshot = getDreamsCollection().get().await()
            val cloudDreams = cloudSnapshot.documents.associate { doc ->
                doc.id to doc.toObject(DreamFirestore::class.java)
            }

            // Синхронизируем локальные сны с облаком
            localDreams.forEach { localDream ->
                val cloudDream = cloudDreams[localDream.id.toString()]
                if (cloudDream == null) {
                    // Добавляем новый сон в облако
                    addDream(localDream)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Класс для сериализации/десериализации в Firestore
data class DreamFirestore(
    val id: Long = 0,
    val date: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val title: String = "",
    val content: String = "",
    val tags: List<String> = emptyList(),
    val isLucid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toDream(): Dream {
        return Dream(
            id = id,
            date = LocalDate.parse(date),
            startTime = LocalTime.parse(startTime),
            endTime = LocalTime.parse(endTime),
            title = title,
            content = content,
            tags = tags,
            isLucid = isLucid
        )
    }

    companion object {
        fun fromDream(dream: Dream): DreamFirestore {
            return DreamFirestore(
                id = dream.id,
                date = dream.date.toString(),
                startTime = dream.startTime.toString(),
                endTime = dream.endTime.toString(),
                title = dream.title,
                content = dream.content,
                tags = dream.tags,
                isLucid = dream.isLucid
            )
        }
    }
} 
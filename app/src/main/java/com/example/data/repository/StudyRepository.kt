package com.example.data.repository

import com.example.data.dao.StudyDao
import com.example.data.model.SubjectWithItems
import kotlinx.coroutines.flow.Flow

class StudyRepository(
    private val studyDao: StudyDao
) {
    val allSubjectsWithItems: Flow<List<SubjectWithItems>> =
        studyDao.getAllSubjectsWithItems()

    suspend fun toggleItemCompletion(itemId: Long, currentCompleted: Boolean) {
        val newStatus = !currentCompleted
        val completedAt = if (newStatus) System.currentTimeMillis() else null
        studyDao.updateItemCompletion(itemId, newStatus, completedAt)
    }

    suspend fun resetAllProgress() {
        studyDao.clearStudyItems()
        studyDao.clearSubjects()
    }
}

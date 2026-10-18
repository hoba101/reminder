package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.model.StudyItem
import com.example.data.model.Subject
import com.example.data.model.SubjectWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    @Transaction
    @Query("SELECT * FROM subjects ORDER BY displayOrder ASC")
    fun getAllSubjectsWithItems(): Flow<List<SubjectWithItems>>

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<Subject>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyItems(items: List<StudyItem>)

    @Query("UPDATE study_items SET isCompleted = :isCompleted, completedAt = :completedAt WHERE id = :itemId")
    suspend fun updateItemCompletion(itemId: Long, isCompleted: Boolean, completedAt: Long?)

    @Query("DELETE FROM subjects")
    suspend fun clearSubjects()

    @Query("DELETE FROM study_items")
    suspend fun clearStudyItems()
}

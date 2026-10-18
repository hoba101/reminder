package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "subjects",
    indices = [Index(value = ["code"], unique = true)]
)
data class Subject(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val hasTheory: Boolean = true,
    val hasPractical: Boolean = false,
    val iconKey: String,
    val accentColorHex: String = "#53735D",
    val displayOrder: Int = 0,
    val keyConcepts: String = "", // من الصورة: أهم المفاهيم والموضوعات
    val learningGoal: String = "" // من الصورة: ماذا يجب أن تفهم؟
)

@Entity(
    tableName = "study_items",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class StudyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val title: String, // اسم الفصل أو الموضوع المحدد من الصورة
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val note: String = "",
    val completedAt: Long? = null
)

data class SubjectWithItems(
    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val items: List<StudyItem>
) {
    val sortedItems: List<StudyItem>
        get() = items.sortedBy { it.orderIndex }

    val completedCount: Int
        get() = items.count { it.isCompleted }

    val remainingCount: Int
        get() = items.count { !it.isCompleted }

    val totalCount: Int
        get() = items.size

    val progressFraction: Float
        get() = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val progressPercent: Int
        get() = (progressFraction * 100).toInt()

    val isAllCompleted: Boolean
        get() = totalCount > 0 && completedCount == totalCount
}

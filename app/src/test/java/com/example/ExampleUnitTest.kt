package com.example

import com.example.data.model.StudyItem
import com.example.data.model.Subject
import com.example.data.model.SubjectWithItems
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurriculumCompletionCalculation() {
        val subject = Subject(
            id = 1,
            name = "كيمياء فيزيائية",
            code = "CHM 111",
            iconKey = "chemistry"
        )
        val items = listOf(
            StudyItem(id = 1, subjectId = 1, title = "مبادئ الديناميكا الحرارية", isCompleted = true),
            StudyItem(id = 2, subjectId = 1, title = "الاتزان الكيميائي", isCompleted = true),
            StudyItem(id = 3, subjectId = 1, title = "سرعة التفاعلات الكيميائية", isCompleted = false),
            StudyItem(id = 4, subjectId = 1, title = "الخواص الفيزيائية للمحاليل", isCompleted = false)
        )

        val subjectWithItems = SubjectWithItems(subject, items)

        assertEquals(4, subjectWithItems.totalCount)
        assertEquals(2, subjectWithItems.completedCount)
        assertEquals(2, subjectWithItems.remainingCount)
        assertEquals(50, subjectWithItems.progressPercent)
        assertFalse(subjectWithItems.isAllCompleted)
    }

    @Test
    fun testAllCompletedCalculation() {
        val subject = Subject(
            id = 2,
            name = "حاسب آلي",
            code = "CSC 111",
            iconKey = "computer"
        )
        val items = listOf(
            StudyItem(id = 10, subjectId = 2, title = "أساسيات الحاسب الآلي وأنظمته", isCompleted = true),
            StudyItem(id = 11, subjectId = 2, title = "أنظمة التشغيل", isCompleted = true)
        )

        val subjectWithItems = SubjectWithItems(subject, items)

        assertEquals(2, subjectWithItems.completedCount)
        assertEquals(100, subjectWithItems.progressPercent)
        assertTrue(subjectWithItems.isAllCompleted)
    }
}

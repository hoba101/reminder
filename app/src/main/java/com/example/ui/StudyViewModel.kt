package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.SubjectWithItems
import com.example.data.repository.StudyRepository
import com.example.notifications.StudyNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ChecklistFilter {
    ALL,
    PENDING,
    COMPLETED
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository
    val allSubjects: StateFlow<List<SubjectWithItems>>

    private val _selectedFilter = MutableStateFlow(ChecklistFilter.ALL)
    val selectedFilter: StateFlow<ChecklistFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isReminderActive = MutableStateFlow(true)
    val isReminderActive: StateFlow<Boolean> = _isReminderActive.asStateFlow()

    private val _reminderIntervalHours = MutableStateFlow(3)
    val reminderIntervalHours: StateFlow<Int> = _reminderIntervalHours.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = StudyRepository(db.studyDao())

        // Defensive distinctBy to ensure no subject can EVER be duplicated
        allSubjects = repository.allSubjectsWithItems.map { list ->
            list.distinctBy { it.subject.name.trim() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

        // Setup notification channel and schedule reminder
        StudyNotificationHelper.createNotificationChannel(application)
        StudyNotificationHelper.schedulePeriodicReminders(application, 3)
    }

    val filteredSubjects: StateFlow<List<SubjectWithItems>> = combine(
        allSubjects,
        _selectedFilter,
        _searchQuery
    ) { subjects, filter, query ->
        val queryClean = query.trim().lowercase()

        subjects.map { subjectWithItems ->
            val matchingItems = subjectWithItems.items.filter { item ->
                val matchesFilter = when (filter) {
                    ChecklistFilter.ALL -> true
                    ChecklistFilter.PENDING -> !item.isCompleted
                    ChecklistFilter.COMPLETED -> item.isCompleted
                }
                val matchesQuery = queryClean.isEmpty() ||
                        item.title.lowercase().contains(queryClean) ||
                        subjectWithItems.subject.name.lowercase().contains(queryClean) ||
                        subjectWithItems.subject.code.lowercase().contains(queryClean)

                matchesFilter && matchesQuery
            }
            subjectWithItems.copy(items = matchingItems)
        }.filter {
            if (queryClean.isEmpty() && filter == ChecklistFilter.ALL) {
                true
            } else {
                it.items.isNotEmpty() || (queryClean.isNotEmpty() && (
                        it.subject.name.lowercase().contains(queryClean) ||
                        it.subject.code.lowercase().contains(queryClean)
                ))
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun toggleItem(itemId: Long, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleItemCompletion(itemId, currentCompleted)
            _userMessage.value = if (!currentCompleted) {
                "أحسنتِ يا بطلة! تم تسجيل هذا الموضوع كـ منجز"
            } else {
                "تمت إعادة الموضوع إلى قائمة المتبقي"
            }
        }
    }

    fun setFilter(filter: ChecklistFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleReminders(context: Context, enabled: Boolean) {
        _isReminderActive.value = enabled
        if (enabled) {
            StudyNotificationHelper.schedulePeriodicReminders(context, _reminderIntervalHours.value)
            _userMessage.value = "تم تفعيل التذكيرات اللطيفة كل ${_reminderIntervalHours.value} ساعات"
        } else {
            StudyNotificationHelper.cancelReminders(context)
            _userMessage.value = "تم إيقاف التذكيرات مؤقتاً"
        }
    }

    fun sendTestNotification(context: Context) {
        StudyNotificationHelper.sendStudyReminder(context)
        _userMessage.value = "تم إرسال إشعار تذكيري لطيف لهاتفكِ الآن"
    }

    fun dismissMessage() {
        _userMessage.value = null
    }
}

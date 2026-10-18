package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StudyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        StudyNotificationHelper.sendStudyReminder(context)
    }
}

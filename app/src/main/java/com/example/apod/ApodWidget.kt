package com.example.apod

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/**
 * Implementation of App Widget functionality.
 */
class ApodWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val immediateRequest = OneTimeWorkRequestBuilder<ApodUpdateWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(immediateRequest)

        // Schedule a one-time immediate update and a daily periodic update
        WorkScheduler.scheduleApodWork(context)
    }
}

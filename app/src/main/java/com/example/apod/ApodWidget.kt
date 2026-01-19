package com.example.apod

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class ApodWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val immediateRequest = OneTimeWorkRequestBuilder<ApodUpdateWorker>()
            .build()
        WorkManager.getInstance(context).enqueue(immediateRequest)

        // Schedule a one-time immediate update and a daily periodic update
        scheduleDailyUpdate(context)
    }

    private fun scheduleDailyUpdate(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ApodUpdateWorker>(6, TimeUnit.HOURS)
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "APOD_UPDATE",
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }
}

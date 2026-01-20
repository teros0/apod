package com.example.apod

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import java.lang.Exception
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class ApodUpdateWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Replace with your actual key or use BuildConfig for safety
            val apiKey = BuildConfig.NASA_API_KEY
            val response = RetrofitInstance.api.getTodayImage(apiKey)

            val finalResponse = when {
                response.media_type == "image" -> response
                response.media_type == "video" && response.url.contains("youtube.com") -> {
                    val videoId = extractYoutubeId(response.url)
                    response.copy(url = "https://img.youtube.com/vi/$videoId/0.jpg")
                }
                else -> return@withContext Result.success() // Handle non-supported media
            }

            updateWidgetAndCache(finalResponse)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun updateWidgetAndCache(apod: ApodResponse) {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
        val componentName = ComponentName(applicationContext, ApodWidgetProvider::class.java)
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.apod_widget)

        try {
            // Try HD first, fall back to standard URL if null
            val imageUrl = apod.hdurl ?: apod.url

            val bitmap = Glide.with(applicationContext)
                .asBitmap()
                .load(imageUrl)
                .skipMemoryCache(true)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                .submit(512, 512)
                .get()

            saveApodLocally(apod, bitmap)

            // force a clear state
            remoteViews.setImageViewResource(R.id.widget_image, android.R.color.transparent)
            remoteViews.setImageViewBitmap(R.id.widget_image, bitmap)

            remoteViews.setTextViewText(R.id.widget_title, apod.title)

            val intent = Intent(applicationContext, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            remoteViews.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(componentName, remoteViews)
        } catch (e: Exception) {
            // Optional: Set an error layout in RemoteViews here
        }
    }

    private fun saveApodLocally(apod: ApodResponse, bitmap: Bitmap) {
        applicationContext.getSharedPreferences("apod_prefs", Context.MODE_PRIVATE).edit().apply {
            putString("title", apod.title)
            putString("explanation", apod.explanation)
            putString("url", apod.url) // Storing the image/thumbnail URL
            apply()
        }

        applicationContext.openFileOutput("today_image.png", Context.MODE_PRIVATE).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun extractYoutubeId(url: String): String {
        return when {
            url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?")
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            else -> url.substringAfterLast("/")
        }
    }
}

object WorkScheduler {
    fun scheduleApodWork(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ApodUpdateWorker>(6, TimeUnit.HOURS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "APOD_UPDATE",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
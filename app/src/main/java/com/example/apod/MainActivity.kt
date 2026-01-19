package com.example.apod

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.io.File
import androidx.core.net.toUri
import androidx.work.ExistingWorkPolicy

class MainActivity : AppCompatActivity() {

    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        swipeRefresh = findViewById(R.id.swipeRefresh)

        // Window Insets handling
        ViewCompat.setOnApplyWindowInsetsListener(swipeRefresh) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("apod_prefs", Context.MODE_PRIVATE)

        // Refresh Listener
        swipeRefresh.setOnRefreshListener {
            triggerUpdate(ExistingWorkPolicy.REPLACE)
        }

        // Initial check
        if (!prefs.contains("title")) {
            triggerUpdate(ExistingWorkPolicy.KEEP)
        }

        displayCachedData()
    }

    private fun triggerUpdate(policy: ExistingWorkPolicy) {
        val workRequest = OneTimeWorkRequestBuilder<ApodUpdateWorker>().build()

        swipeRefresh.isRefreshing = true

        WorkManager.getInstance(this).enqueueUniqueWork(
            "APOD_FETCH_TASK",
            policy,
            workRequest
        )

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(workRequest.id)
            .observe(this) { info ->
                if (info?.state?.isFinished == true) {
                    swipeRefresh.isRefreshing = false
                    displayCachedData()
                }
            }
    }

    private fun displayCachedData() {
        val prefs = getSharedPreferences("apod_prefs", Context.MODE_PRIVATE)
        val title = prefs.getString("title", "No Data")
        val explanation = prefs.getString("explanation", "")
        val url = prefs.getString("url", "") ?: ""

        findViewById<TextView>(R.id.main_title).text = title
        findViewById<TextView>(R.id.main_explanation).text = explanation

        val imageFile = File(filesDir, "today_image.png")
        findViewById<ImageView>(R.id.main_image).apply {
            if (imageFile.exists()) {
                setImageURI(imageFile.toUri())
                setOnClickListener {
                    handleImageClick(url)
                }
            }
        }
    }

    private fun handleImageClick(url: String) {
        if (url.contains("youtube.com") || url.contains("img.youtube.com")) {
            // Open APOD page for videos
            val intent = Intent(Intent.ACTION_VIEW,
                "https://apod.nasa.gov/apod/astropix.html".toUri())
            startActivity(intent)
        } else {
            // Open full screen for images
            val intent = Intent(this, FullScreenImageActivity::class.java)
            startActivity(intent)
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

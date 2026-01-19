package com.example.apod

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView
import java.io.File

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val photoView = findViewById<PhotoView>(R.id.photo_view)
        val closeButton = findViewById<ImageButton>(R.id.close_button)

        // Load the locally stored image
        val file = File(filesDir, "today_image.png")
        if (file.exists()) {
            photoView.setImageURI(android.net.Uri.fromFile(file))
        }

        closeButton.setOnClickListener { finish() }
    }
}

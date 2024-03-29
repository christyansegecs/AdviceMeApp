package com.chris.adviceapp.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.chris.adviceapp.databinding.ActivityImageBinding
import java.io.File
import java.io.FileOutputStream

class ImageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val advice = intent.getStringExtra("share")

        binding.tvShare.text = advice
        setupOnClickListener()

    }

    private fun setupOnClickListener() {
        binding.btnShare.setOnClickListener {
            loadBitmapFromView(binding.cvShare)
        }
    }

    private fun loadBitmapFromView(v: View): Bitmap? {
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val returnedBitmap = Bitmap.createBitmap(
            v.measuredWidth,
            v.measuredHeight, Bitmap.Config.ARGB_8888
        )
        val c = Canvas(returnedBitmap)
        v.draw(c)
        binding.cvShare.isVisible = false
        binding.ivTest.setImageBitmap(returnedBitmap)
        shareImage(returnedBitmap)
        return returnedBitmap
    }

    private fun shareImage(b: Bitmap) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val file = File(externalCacheDir.toString() + "/" + "Advice" + ".png")
        val intent: Intent
        try {
            val outputStream = FileOutputStream(file)
            b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
        startActivity(Intent.createChooser(intent, "Share image via: "))
    }
}
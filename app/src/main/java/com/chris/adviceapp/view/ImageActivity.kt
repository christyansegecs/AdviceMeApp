package com.chris.adviceapp.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.view.View.MeasureSpec
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.databinding.ActivityImageBinding
import com.chris.adviceapp.util.ImageState
import com.chris.adviceapp.viewmodel.ImageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


class ImageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityImageBinding
    private val imageViewModel : ImageViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        imageViewModel.getPictureFromText()

        handlePicture()
        val advice = intent.getStringExtra("share")

        binding.tvShare.text = advice
        setupOnClickListener()

    }

    private fun setupOnClickListener() {
        binding.btnShare.setOnClickListener {
            getScreenViewBitmap(binding.cvShare)
        }
    }

    private fun getScreenViewBitmap(v: View) {
        v.isDrawingCacheEnabled = true
        v.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        v.buildDrawingCache(true)
        val b = Bitmap.createBitmap(v.drawingCache)
        v.isDrawingCacheEnabled = false
        shareImage(b)
    }

    private fun shareImage(b: Bitmap) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val file = File(externalCacheDir.toString() + "/" + "Advice" + "png")
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

    private fun handlePicture() = lifecycleScope.launchWhenCreated{
        imageViewModel._imageStateFlow.collect {
            when(it) {
                is ImageState.onLoading -> {}
                is ImageState.onError -> {}
                is ImageState.onSuccess -> {}
                is ImageState.Empty -> {}
            }
        }
    }
}
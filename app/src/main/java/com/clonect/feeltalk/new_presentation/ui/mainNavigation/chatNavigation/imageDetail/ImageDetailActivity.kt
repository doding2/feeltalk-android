package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ActivityImageDetailBinding
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.extendRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.presentation.utils.infoLog
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.snackbar.Snackbar
import com.skydoves.transformationlayout.TransformationAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ImageDetailActivity : TransformationAppCompatActivity() {

    private lateinit var binding: ActivityImageDetailBinding
    private val viewModel: ImageDetailViewModel by viewModels()
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        extendRootViewLayout(window)
        setLightStatusBars(false, this, binding.root)
        binding.llActionBar.setPadding(0, getStatusBarHeight(), 0, 0)

        loadingDialog = makeLoadingDialog()

        val imageChat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("imageChat", ImageChat::class.java)
        } else {
            intent.getParcelableExtra("imageChat")
        }
        viewModel.setImageChat(imageChat)


        collectViewModel()

        binding.run {
            ssivImage.maxScale = 10f
            ssivImage.setDoubleTapZoomScale(2f)
            ssivImage.setDoubleTapZoomDuration(150)
            ivExit.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            ivDownload.setOnClickListener { downloadImage() }
        }
    }


    private fun downloadImage() {
        viewModel.downloadImage {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun applyImageChatChanges(imageChat: ImageChat?) = lifecycleScope.launch {
        binding.run {
            tvNickname.text = imageChat?.chatSender
            tvDate.text = imageChat?.createAt

            if (imageChat == null) return@launch

            val result = withContext(Dispatchers.IO) {
                Glide.with(this@ImageDetailActivity).run {
                    if (imageChat.file != null) {
                        load(imageChat.file)
                    } else if (imageChat.url != null) {
                        load(imageChat.url)
                    } else {
                        load(imageChat.uri)
                    }
                }.placeholder(R.drawable.n_background_image_detail_placeholder)
                    .error(R.drawable.n_background_image_detail_placeholder)
                    .fallback(R.drawable.n_background_image_detail_placeholder)
                    .submit()
                    .get()
            }

            ssivImage.setImage(ImageSource.cachedBitmap(result.toBitmap()))
        }
    }


    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show()
        } else {
            loadingDialog.dismiss()
        }
    }

    private fun showSnackBar(message: String) {
        val decorView = window?.decorView ?: return
        TextSnackbar.make(
            view = decorView,
            message = message,
            duration = Snackbar.LENGTH_SHORT,
            onClick = {
                it.dismiss()
            }
        ).show()
    }

    private fun collectViewModel() = lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.isLoading.collectLatest(::showLoading) }
            launch { viewModel.errorMessage.collectLatest(::showSnackBar) }
            launch { viewModel.imageChat.collectLatest(::applyImageChatChanges) }
        }
    }


}
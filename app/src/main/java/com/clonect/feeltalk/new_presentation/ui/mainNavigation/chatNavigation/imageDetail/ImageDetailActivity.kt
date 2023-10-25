package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.clonect.feeltalk.databinding.ActivityImageDetailBinding
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageShare.ImageShareViewModel
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.clonect.feeltalk.new_presentation.ui.util.stateFlow
import com.clonect.feeltalk.new_presentation.ui.util.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.otaliastudios.zoom.ZoomLayout
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
    private lateinit var onBackCallback: OnBackPressedCallback
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = makeLoadingDialog()
        registerBackCallback()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            binding.root.setPadding(0, getStatusBarHeight(), 0, 0)
            setLightStatusBars(false, this, binding.root)
        }

        viewModel.imageChat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("imageChat", ImageChat::class.java)
        } else {
            intent.getParcelableExtra("imageChat")
        }


        collectViewModel()

        binding.run {
            ivExit.setOnClickListener { onBackCallback.handleOnBackPressed() }
            ivDownload.setOnClickListener { downloadImage() }

            zlImage.registerDoubleTouchReset()
        }
    }

    private fun downloadImage() {
        viewModel.downloadImage {
            onBackCallback.handleOnBackPressed()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ZoomLayout.registerDoubleTouchReset() {
        val detector = GestureDetector(this@ImageDetailActivity, GestureDetector.SimpleOnGestureListener())
        detector.setOnDoubleTapListener(object: GestureDetector.OnDoubleTapListener {
            override fun onSingleTapConfirmed(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTap(p0: MotionEvent): Boolean { return true }
            override fun onDoubleTapEvent(p0: MotionEvent): Boolean {
                engine.zoomTo(1f, true)
                return true
            }
        })

        setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            false
        }
    }


    private fun applyImageChatChanges(imageChat: ImageChat?) = lifecycleScope.launch {
        binding.run {
            tvNickname.text = imageChat?.chatSender
            tvDate.text = imageChat?.createAt

            val bitmap = withContext(Dispatchers.IO) { imageChat?.uri?.toBitmap(this@ImageDetailActivity) }
            ivImage.setImageBitmap(bitmap)
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
            launch { viewModel::imageChat.stateFlow.collectLatest(::applyImageChatChanges) }
        }
    }

    private fun registerBackCallback() {
        onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackCallback.remove()
    }
}
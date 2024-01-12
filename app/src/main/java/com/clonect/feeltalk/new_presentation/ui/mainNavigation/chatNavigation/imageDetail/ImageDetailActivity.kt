package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.databinding.ActivityImageDetailBinding
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_presentation.ui.util.TextSnackbar
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.new_presentation.ui.util.extendRootViewLayout
import com.clonect.feeltalk.new_presentation.ui.util.getStatusBarHeight
import com.clonect.feeltalk.new_presentation.ui.util.makeLoadingDialog
import com.clonect.feeltalk.new_presentation.ui.util.setLightStatusBars
import com.davemorrissey.labs.subscaleview.ImageSource
import com.google.android.material.snackbar.Snackbar
import com.skydoves.transformationlayout.TransformationAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

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
        viewModel.navigatePage()

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
        viewModel.downloadImage(this) {
//            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun applyImageChatChanges(imageChat: ImageChat?) = lifecycleScope.launch {
        binding.run {
            if (imageChat?.chatSender == "me") {
                tvNickname.text = viewModel.myInfo.value?.nickname
                applyMySignalChanges(viewModel.mySignal.value)
            }
            if (imageChat?.chatSender == "partner") {
                tvNickname.text = viewModel.partnerInfo.value?.nickname
                applyPartnerSignalChanges(viewModel.partnerSignal.value)
            }

            if (imageChat?.createAt != null) {
                val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = serverFormat.parse(imageChat.createAt)
                val clientFormat = SimpleDateFormat(getString(R.string.image_detail_date_format), Locale.getDefault())
                tvDate.text = clientFormat.format(date)
            }

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
                }.submit().get().toBitmap()
            }

            ssivImage.setImage(ImageSource.cachedBitmap(result))
        }
    }

    private fun applyMyInfoChanges(myInfo: MyInfo?) = binding.run {
        if (myInfo == null || viewModel.imageChat.value?.chatSender != "me") return
        tvNickname.text = myInfo.nickname
    }

    private fun applyPartnerInfoChanges(partnerInfo: PartnerInfo?) = binding.run {
        if (partnerInfo == null || viewModel.imageChat.value?.chatSender != "partner") return
        tvNickname.text = partnerInfo.nickname
    }

    private fun applyMySignalChanges(signal: Signal) = binding.run {
        if (viewModel.imageChat.value?.chatSender != "me") return
        val signalRes = when (signal) {
            Signal.One -> R.drawable.n_image_signal_100
            Signal.ThreeFourth -> R.drawable.n_image_signal_75
            Signal.Half -> R.drawable.n_image_signal_50
            Signal.Quarter -> R.drawable.n_image_signal_25
            Signal.Zero -> R.drawable.n_image_signal_0
        }
        ivSignal.setImageResource(signalRes)
    }

    private fun applyPartnerSignalChanges(signal: Signal) = binding.run {
        if (viewModel.imageChat.value?.chatSender != "partner") return
        val signalRes = when (signal) {
            Signal.One -> R.drawable.n_image_signal_100
            Signal.ThreeFourth -> R.drawable.n_image_signal_75
            Signal.Half -> R.drawable.n_image_signal_50
            Signal.Quarter -> R.drawable.n_image_signal_25
            Signal.Zero -> R.drawable.n_image_signal_0
        }
        ivSignal.setImageResource(signalRes)
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
            bottomMargin = applicationContext.dpToPx(56f),
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
            launch { viewModel.myInfo.collectLatest(::applyMyInfoChanges) }
            launch { viewModel.partnerInfo.collectLatest(::applyPartnerInfoChanges) }
            launch { viewModel.mySignal.collectLatest(::applyMySignalChanges) }
            launch { viewModel.partnerSignal.collectLatest(::applyPartnerSignalChanges) }
        }
    }


}
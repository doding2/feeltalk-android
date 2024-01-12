package com.clonect.feeltalk.new_presentation.ui.mainNavigation.chatNavigation.imageDetail

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.clonect.feeltalk.R
import com.clonect.feeltalk.common.onError
import com.clonect.feeltalk.common.onSuccess
import com.clonect.feeltalk.new_domain.model.account.MyInfo
import com.clonect.feeltalk.new_domain.model.chat.ImageChat
import com.clonect.feeltalk.new_domain.model.partner.PartnerInfo
import com.clonect.feeltalk.new_domain.model.signal.Signal
import com.clonect.feeltalk.new_domain.usecase.account.GetMyInfoUseCase
import com.clonect.feeltalk.new_domain.usecase.chat.PreloadImageUseCase
import com.clonect.feeltalk.new_domain.usecase.mixpanel.NavigatePageMixpanelUseCase
import com.clonect.feeltalk.new_domain.usecase.partner.GetPartnerInfoFlowUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetMySignalUseCase
import com.clonect.feeltalk.new_domain.usecase.signal.GetPartnerSignalFlowUseCase
import com.clonect.feeltalk.presentation.utils.infoLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

/**
 * Created by doding2 on 2023/10/25.
 */
@HiltViewModel
class ImageDetailViewModel @Inject constructor(
    private val getMyInfoUseCase: GetMyInfoUseCase,
    private val getPartnerInfoFlowUseCase: GetPartnerInfoFlowUseCase,
    private val getMySignalUseCase: GetMySignalUseCase,
    private val getPartnerSignalFlowUseCase: GetPartnerSignalFlowUseCase,
    private val preloadImageUseCase: PreloadImageUseCase,
    private val navigatePageMixpanelUseCase: NavigatePageMixpanelUseCase,
) : ViewModel() {

    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage = _errorMessage.asSharedFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _imageChat = MutableStateFlow<ImageChat?>(null)
    val imageChat = _imageChat.asStateFlow()

    private val _myInfo: MutableStateFlow<MyInfo?> = MutableStateFlow(null)
    val myInfo = _myInfo.asStateFlow()

    private val _partnerInfo: MutableStateFlow<PartnerInfo?> = MutableStateFlow(null)
    val partnerInfo = _partnerInfo.asStateFlow()

    private val _mySignal: MutableStateFlow<Signal> = MutableStateFlow(Signal.One)
    val mySignal = _mySignal.asStateFlow()

    private val _partnerSignal: MutableStateFlow<Signal> = MutableStateFlow(Signal.One)
    val partnerSignal = _partnerSignal.asStateFlow()

    init {
        getMyInfo()
        getPartnerInfoFlow()
        getMySignal()
        getPartnerSignalFlow()
    }


    fun sendErrorMessage(message: String) = viewModelScope.launch {
        _errorMessage.emit(message)
    }

    fun setLoading(isLoading: Boolean) = viewModelScope.launch {
        _isLoading.value = isLoading
    }

    fun setImageChat(imageChat: ImageChat?) {
        _imageChat.value = imageChat
    }


    fun getMyInfo() = viewModelScope.launch {
        getMyInfoUseCase()
            .onSuccess { _myInfo.value = it }
            .onError { it.localizedMessage?.let { it1 -> infoLog(it1) } }
    }

    fun getPartnerInfoFlow() = viewModelScope.launch {
        getPartnerInfoFlowUseCase()
            .collectLatest { result ->
                result.onSuccess {
                    _partnerInfo.value = it
                }.onError {
                    it.localizedMessage?.let { it1 -> infoLog(it1) }
                }
            }
    }

    fun getMySignal() = viewModelScope.launch {
        getMySignalUseCase()
            .onSuccess { _mySignal.value = it }
            .onError { it.localizedMessage?.let { it1 -> infoLog(it1) } }
    }

    fun getPartnerSignalFlow() = viewModelScope.launch {
        getPartnerSignalFlowUseCase()
            .collectLatest {
                _partnerSignal.value = it ?: Signal.One
            }
    }


    fun downloadImage(context: Context, onComplete: () -> Unit) = viewModelScope.launch {
        val chat = imageChat.value ?: return@launch
        setLoading(true)

        try {
            val cacheFile = File(context.cacheDir, "${chat.index}.png")
            val bitmap = withContext(Dispatchers.IO) {
                Glide.with(context)
                    .asBitmap()
                    .run {
                        if (cacheFile.exists() && cacheFile.canRead()) {
                            load(cacheFile)
                        } else if (chat.url != null) {
                            load(chat.url)
                        } else {
                            load(chat.uri)
                        }
                    }.submit()
                    .get()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileUsingMediaStore(context, "pillowtalk_${chat.index}.png", bitmap)
            } else {
                saveFileUsingLegacy(context, "pillowtalk_${chat.index}.png", bitmap)
            }

            sendErrorMessage(context.getString(R.string.image_detail_download_succeed))
            onComplete()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            infoLog("Fail to download image: ${e.localizedMessage}")
            sendErrorMessage(context.getString(R.string.pillowtalk_default_error_message))
        } finally {
            setLoading(false)
        }
    }

    private fun saveFileUsingLegacy(context: Context, fileName: String, bitmap: Bitmap) {
        val downloadDirFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val imageFile = File(downloadDirFile, fileName)

        imageFile.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }
        notifyImageDownloaded(context, imageFile)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileUsingMediaStore(context: Context, fileName: String, bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            resolver.openOutputStream(uri)?.use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.flush()
            }
            notifyImageDownloaded(context, uri)
        }
    }

    private fun notifyImageDownloaded(context: Context, file: File) {
        MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("image/png"), null)
    }

    private fun notifyImageDownloaded(context: Context, uri: Uri) {
        MediaScannerConnection.scanFile(context, arrayOf(uri.path), arrayOf("image/png"), null)
    }


    fun navigatePage() = viewModelScope.launch {
        navigatePageMixpanelUseCase()
    }
}
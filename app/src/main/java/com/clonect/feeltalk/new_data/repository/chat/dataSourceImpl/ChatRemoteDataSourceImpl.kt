package com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl

import android.accounts.NetworkErrorException
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_domain.model.chat.*
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.clonect.feeltalk.common.FeelTalkException.ServerIsDownException
import com.clonect.feeltalk.new_presentation.ui.util.dpToPx
import com.clonect.feeltalk.presentation.utils.infoLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class ChatRemoteDataSourceImpl(
    private val context: Context,
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    override suspend fun getPartnerLastChat(accessToken: String): PartnerLastChatDto {
        val response = clonectService.getPartnerLastChat(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean) {
        val body = JsonObject().apply {
            addProperty("isInChat", isInChat)
        }
        val response = clonectService.changeChatRoomState(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getLastChatPageNo(accessToken: String): LastChatPageNoDto {
        val response = clonectService.getLastChatPageNo(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getChatList(accessToken: String, pageNo: Long): ChatListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getChatList(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun sendTextChat(accessToken: String, message: String): SendTextChatDto {
        val body = JsonObject().apply {
            addProperty("message", message)
        }
        val response = clonectService.sendTextChat(accessToken, body)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun sendVoiceChat(accessToken: String, voiceFile: File): SendVoiceChatDto {
        val data = MultipartBody.Part.createFormData(
            name = "voiceFile",
            filename = voiceFile.name,
            body = voiceFile.asRequestBody(
                "audio/*".toMediaTypeOrNull()
            )
        )
        val response = clonectService.sendVoiceChat(accessToken, data)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun sendImageChat(
        accessToken: String,
        imageFile: File,
    ): SendImageChatResponse {
        val data = MultipartBody.Part.createFormData(
            name = "imageFile",
            filename = imageFile.name,
            body = imageFile.asRequestBody(
                "image/*".toMediaTypeOrNull()
            )
        )
        val response = clonectService.sendImageChat(accessToken, data)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun sendResetPartnerPasswordChat(accessToken: String): SendResetPartnerPasswordChatResponse {
        val response = clonectService.sendResetPartnerPasswordChat(accessToken)
        if (!response.isSuccessful) throw ServerIsDownException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun preloadImage(index: Long, url: String): Triple<File?, Int, Int> {
        return withContext(Dispatchers.IO) {
            try {
                val urlObj = URL(url)
                val bitmap = BitmapFactory.decodeStream(urlObj.openConnection().getInputStream())

                val imageFile = File(context.cacheDir, "${index}.png")
                if (!imageFile.exists() || !imageFile.canRead()) {
                    imageFile.outputStream().use {
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, it)
                        it.flush()
                    }
                }

                val width = bitmap.width
                val height = bitmap.height

                val maxWidth = context.dpToPx(252f).toFloat()
                val maxHeight = context.dpToPx(300f).toFloat()
                var mWidth = width.takeIf { it > 0 } ?: maxWidth.toInt()
                var mHeight = height.takeIf { it > 0 } ?: maxHeight.toInt()

                val heightScale = if (mHeight > maxHeight) maxHeight / mHeight else mHeight / maxHeight
                mWidth = (mWidth * heightScale).toInt()
                mHeight = (mHeight * heightScale).toInt()

                val widthScale = if (mWidth > maxWidth) maxWidth / mWidth else mWidth / maxWidth
                mWidth = (mWidth * widthScale).toInt()
                mHeight = (mHeight * widthScale).toInt()

                Triple(imageFile, mWidth, mHeight)
            } catch (e: Exception) {
                infoLog("Fail to preload image: ${e.localizedMessage}")
            }

            Triple(null, 252, 300)
        }
    }

}
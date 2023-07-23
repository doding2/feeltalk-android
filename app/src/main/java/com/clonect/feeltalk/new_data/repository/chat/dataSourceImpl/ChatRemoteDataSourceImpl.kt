package com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_domain.model.chat.*
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class ChatRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    override suspend fun getPartnerLastChat(accessToken: String): PartnerLastChatDto {
        val response = clonectService.getPartnerLastChat("Bearer $accessToken")
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean) {
        val body = JsonObject().apply {
            addProperty("isInChat", isInChat)
        }
        val response = clonectService.changeChatRoomState("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
    }

    override suspend fun getLastChatPageNo(accessToken: String): LastChatPageNoDto {
        val response = clonectService.getLastChatPageNo("Bearer $accessToken")
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun getChatList(accessToken: String, pageNo: Long): ChatListDto {
        val body = JsonObject().apply {
            addProperty("pageNo", pageNo)
        }
        val response = clonectService.getChatList("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

    override suspend fun sendTextChat(accessToken: String, message: String): SendTextChatDto {
        val body = JsonObject().apply {
            addProperty("message", message)
        }
        val response = clonectService.sendTextChat("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
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

        val response = clonectService.sendVoiceChat("Bearer $accessToken", data)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
        if (response.body()?.status?.lowercase() == "fail") throw NetworkErrorException(response.body()?.message)
        return response.body()!!.data!!
    }

}
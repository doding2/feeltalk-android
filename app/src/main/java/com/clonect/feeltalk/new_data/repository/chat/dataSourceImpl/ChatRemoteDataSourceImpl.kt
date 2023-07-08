package com.clonect.feeltalk.new_data.repository.chat.dataSourceImpl

import android.accounts.NetworkErrorException
import com.clonect.feeltalk.new_data.api.ClonectService
import com.clonect.feeltalk.new_data.repository.chat.dataSource.ChatRemoteDataSource
import com.clonect.feeltalk.new_domain.model.chat.ChatListDto
import com.clonect.feeltalk.new_domain.model.chat.LastChatPageNoDto
import com.clonect.feeltalk.new_domain.model.chat.SendTextChatDto
import com.google.gson.JsonObject
import retrofit2.HttpException

class ChatRemoteDataSourceImpl(
    private val clonectService: ClonectService
): ChatRemoteDataSource {

    override suspend fun changeChatRoomState(accessToken: String, isInChat: Boolean) {
        val body = JsonObject().apply {
            addProperty("isInChat", isInChat)
        }
        val response = clonectService.changeChatRoomState("Bearer $accessToken", body)
        if (!response.isSuccessful) throw HttpException(response)
        if (response.body()?.data == null) throw NullPointerException("Response body from server is null.")
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

}
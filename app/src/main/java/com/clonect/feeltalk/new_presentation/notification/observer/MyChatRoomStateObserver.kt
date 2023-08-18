package com.clonect.feeltalk.new_presentation.notification.observer

class MyChatRoomStateObserver {
    private var isUserInChat = false

    fun getUserInChat() = isUserInChat

    fun setUserInChat(isInChat: Boolean) {
        isUserInChat = isInChat
    }


    companion object {
        private var Instance: MyChatRoomStateObserver? = null

        fun getInstance(): MyChatRoomStateObserver {
            if (Instance == null) {
                Instance = MyChatRoomStateObserver()
                return Instance!!
            }
            return Instance!!
        }

        fun onCleared() {
            Instance = null
        }
    }
}
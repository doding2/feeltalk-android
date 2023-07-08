package com.clonect.feeltalk.new_data.repository.signIn.dataSource

interface SignInCacheDataSource {
    fun saveCoupleCode(coupleCode: String?)
    fun getCoupleCode(): String?

}
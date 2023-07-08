package com.clonect.feeltalk.new_data.repository.signIn.dataSourceImpl

import com.clonect.feeltalk.new_data.repository.signIn.dataSource.SignInCacheDataSource

class SignInCacheDataSourceImpl: SignInCacheDataSource {

    private var coupleCode: String? = null

    override fun saveCoupleCode(coupleCode: String?) {
        this.coupleCode = coupleCode
    }

    override fun getCoupleCode() = coupleCode

}
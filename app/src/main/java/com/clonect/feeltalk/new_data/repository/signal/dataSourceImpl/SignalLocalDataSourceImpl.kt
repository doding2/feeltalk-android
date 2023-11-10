package com.clonect.feeltalk.new_data.repository.signal.dataSourceImpl

import android.content.Context
import com.clonect.feeltalk.new_data.repository.signal.dataSource.SignalLocalDataSource
import com.clonect.feeltalk.new_data.util.AppLevelEncryptHelper

/**
 * Created by doding2 on 2023/11/10.
 */
class SignalLocalDataSourceImpl(
    private val context: Context,
    private val appLevelEncryptHelper: AppLevelEncryptHelper,
) : SignalLocalDataSource {

}
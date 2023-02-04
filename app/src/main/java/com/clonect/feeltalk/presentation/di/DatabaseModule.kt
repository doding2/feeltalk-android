package com.clonect.feeltalk.presentation.di

import android.content.Context
import androidx.room.Room
import com.clonect.feeltalk.BuildConfig
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.FeeltalkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providesCipherSupportFactory(): SupportFactory {
        val passPhrase = SQLiteDatabase.getBytes(BuildConfig.ROOM_DATABASE_PASS_PHRASE.toCharArray())
        return SupportFactory(passPhrase)
    }

    @Provides
    @Singleton
    fun providesFeeltalkDatabase(@ApplicationContext context: Context, supportFactory: SupportFactory): FeeltalkDatabase {
        return Room.databaseBuilder(context, FeeltalkDatabase::class.java, "feeltalkDatabase")
            .openHelperFactory(supportFactory)
            .build()
    }

    @Provides
    @Singleton
    fun providesChatDao(feeltalkDatabase: FeeltalkDatabase): ChatDao {
        return feeltalkDatabase.chatDao()
    }


}
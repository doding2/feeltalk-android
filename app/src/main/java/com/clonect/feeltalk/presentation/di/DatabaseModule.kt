package com.clonect.feeltalk.presentation.di

import android.content.Context
import androidx.room.Room
import com.clonect.feeltalk.data.db.ChatDao
import com.clonect.feeltalk.data.db.FeeltalkDatabase
import com.clonect.feeltalk.data.db.QuestionDao
import com.clonect.feeltalk.data.utils.DatabaseEncryptHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton
import net.sqlcipher.database.*

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun providesCipherSupportFactory(databaseEncryptHelper: DatabaseEncryptHelper): SupportFactory {
//        val passPhrase = SQLiteDatabase.getBytes(BuildConfig.ROOM_DATABASE_PASS_PHRASE.toCharArray())
        return SupportFactory(databaseEncryptHelper.getKey().encoded)
    }

    @Provides
    @Singleton
    fun providesFeeltalkDatabase(@ApplicationContext context: Context, supportFactory: SupportFactory): FeeltalkDatabase {
        return Room.databaseBuilder(context, FeeltalkDatabase::class.java, "feeltalkDatabase.db")
            .openHelperFactory(supportFactory)
            .build()
    }

    @Provides
    @Singleton
    fun providesChatDao(feeltalkDatabase: FeeltalkDatabase): ChatDao {
        return feeltalkDatabase.chatDao()
    }

    @Provides
    @Singleton
    fun providesQuestionDao(feeltalkDatabase: FeeltalkDatabase): QuestionDao {
        return feeltalkDatabase.questionDao()
    }

}
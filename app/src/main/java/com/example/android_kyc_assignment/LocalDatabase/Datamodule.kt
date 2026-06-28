package com.example.android_kyc_assignment.LocalDatabase


import android.content.Context
import androidx.room.Room
import com.example.android_kyc_assignment.data.local.CustomerDatastore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kyc_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCustomerDao(db: AppDatabase): CustomerDatastore = db.customerDao()
}
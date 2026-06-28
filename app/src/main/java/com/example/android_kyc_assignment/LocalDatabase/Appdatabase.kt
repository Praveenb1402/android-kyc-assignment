package com.example.android_kyc_assignment.LocalDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android_kyc_assignment.data.local.CustomerDatastore
import com.example.android_kyc_assignment.data.local.CustomerModel

@Database(
    entities = [CustomerModel::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDatastore
}
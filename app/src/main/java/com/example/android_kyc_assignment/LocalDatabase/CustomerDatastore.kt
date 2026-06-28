package com.example.android_kyc_assignment.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CustomerDatastore {

    @Query("SELECT * FROM customers")
    suspend fun getAllCustomers(): List<CustomerModel>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Int): CustomerModel?

    @Query("UPDATE customers SET verified = 1, selfiePath = :selfiePath WHERE id = :id")
    suspend fun markVerified(id: Int, selfiePath: String)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(customers: List<CustomerModel>)

    @Query("DELETE FROM customers")
    suspend fun clearAll()

    @Query("SELECT MIN(fetchedAt) FROM customers")
    suspend fun getOldestFetchTime(): Long?
}
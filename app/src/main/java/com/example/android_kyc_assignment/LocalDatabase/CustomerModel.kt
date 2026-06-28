package com.example.android_kyc_assignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerModel(
    @PrimaryKey val id: Int,
    val name: String,
    val image: String,
    val iban: String,
    val cardNumber: String,
    val cardType: String,
    val currency: String,
    val accountNumber: String,
    val balance: Double,
    val ifsc: String,
    val verified: Boolean = false,
    val selfiePath: String? = null,
    val birth: String = "",
    val nationality: String = "",
    val fetchedAt: Long = System.currentTimeMillis()
)
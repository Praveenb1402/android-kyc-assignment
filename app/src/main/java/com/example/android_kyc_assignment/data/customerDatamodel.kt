package com.example.android_kyc_assignment.data

data class CustomerDatamodel (
    val id: Int,
    val name: String,
    val image: String,
    val iban: String,
    val cardNumber: String,
    val cardType: String,
    val currency: String,
    val accountNumber: String,
    val balance: Double,
    val ifsc: String,
    var verified: Boolean = false,
    val selfiePath: String?,
    val birth: String,
    val nationality: String
)
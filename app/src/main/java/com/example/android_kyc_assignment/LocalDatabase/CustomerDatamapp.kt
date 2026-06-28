package com.example.android_kyc_assignment.LocalDatabase

import com.example.android_kyc_assignment.data.CustomerDatamodel
import com.example.android_kyc_assignment.data.local.CustomerModel

fun CustomerModel.toModel(): CustomerDatamodel = CustomerDatamodel(
    id = id,
    name = name,
    image = image,
    iban = iban,
    cardNumber = cardNumber,
    cardType = cardType,
    currency = currency,
    accountNumber = accountNumber,
    selfiePath = selfiePath,
    balance = balance,
    ifsc = ifsc,
    verified = verified,
    birth = birth,
    nationality = nationality
)

fun CustomerDatamodel.toEntity(): CustomerModel = CustomerModel(
    id = id,
    name = name,
    image = image,
    iban = iban,
    cardNumber = cardNumber,
    cardType = cardType,
    currency = currency,
    accountNumber = accountNumber,
    selfiePath = selfiePath,
    balance = balance,
    ifsc = ifsc,
    verified = verified,
    fetchedAt = System.currentTimeMillis(),
    birth = birth,
    nationality = nationality
)
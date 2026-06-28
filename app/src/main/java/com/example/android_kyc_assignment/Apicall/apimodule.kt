package com.example.android_kyc_assignment.Apicall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("users") val users: List<UserDto>
)

@Serializable
data class UserDto(
    @SerialName("id")          val id: Int,
    @SerialName("firstName")   val firstName: String,
    @SerialName("lastName")    val lastName: String,
    @SerialName("image")       val image: String,
    @SerialName("birthDate")   val birthDate: String,
    @SerialName("nationality") val nationality: String? = null,
    @SerialName("email")       val email: String,
    @SerialName("phone")       val phone: String,
    @SerialName("address")     val address: AddressDto,
    @SerialName("bank")        val bank: BankDto,


)

@Serializable
data class AddressDto(
    @SerialName("address") val street: String,
    @SerialName("city")    val city: String,
    @SerialName("state")   val state: String,
    @SerialName("country") val country: String
)

@Serializable
data class BankDto(
    @SerialName("iban")       val iban: String,
    @SerialName("cardNumber") val cardNumber: String,
    @SerialName("cardType")   val cardType: String,
    @SerialName("currency")   val currency: String
)

@Serializable
data class IfscDto(
    @SerialName("BANK")   val bank: String,
    @SerialName("BRANCH") val branch: String,
    @SerialName("CITY")   val city: String,
    @SerialName("STATE")  val state: String
)
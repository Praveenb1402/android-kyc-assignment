package com.example.android_kyc_assignment.Apicall

import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val httpClient = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true   // important — DummyJSON has many fields we don't map
            isLenient = true
        })
    }
}
suspend fun resolveIfsc(ifsc: String): IfscDto {
    return httpClient.get("https://ifsc.razorpay.com/$ifsc").body()
}
suspend fun fetchUsers(limit: Int = 20, skip: Int = 0): UsersResponse {

    return httpClient.get("https://dummyjson.com/users") {
        url {
            parameters.append("limit", limit.toString())
            parameters.append("skip", skip.toString())
        }
    }.body()
}
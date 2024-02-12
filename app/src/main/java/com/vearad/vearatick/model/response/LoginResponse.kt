package com.vearad.vearatick.model.response

data class LoginData(
    val email: String,
    val password: String,
    val origin: String= "android"
)

data class LoginResponse(
    val data: Data?,
    val success: String?,
    val message: String?,
    val errors: Errors?,
    val authFailed: AuthFailed?

    )
{
    data class Data(
        val access_token: String?,
        val refresh_token: String?,
        val expires_in: Int?
        )

    data class AuthFailed(
        val message: String?,
        )

    data class Errors(
        val email: List<String>?,
        val password: List<String>?,
    )
}
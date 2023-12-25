package com.vearad.vearatick.model

data class LoginData(
    val email: String,
    val password: String,
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

        )

    data class AuthFailed(
        val message: String?,
        )

    data class Errors(
        val email: List<String>?,
        val password: List<String>?,
    )
}
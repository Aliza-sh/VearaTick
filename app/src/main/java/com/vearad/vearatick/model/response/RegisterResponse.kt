package com.vearad.vearatick.model.response

data class RegisterData(
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val password: String,
    val password_confirmation: String,
    val origin: String= "android"
)

data class RegisterResponse(
    val data: Data?,
    val success: String?,
    val user: User?,
    val message: String?,
    val errors: Errors?,

    )
{

    data class Data(
        val access_token: String?,
        val refresh_token: String?,
        val expires_in: Int?

    )

    data class User(
        val username: String,
        val id: Int
    )

    data class Errors(
        val name: List<String>?,
        val username: List<String>?,
        val email: List<String>?,
        val password: List<String>?,
        val phone: List<String>?,
        val password_confirmation: List<String>?
    )
}
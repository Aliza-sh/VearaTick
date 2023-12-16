package com.vearad.vearatick.model

data class LoginData(
    val email: String,
    val password: String,
)

data class LoginResponse(
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

        )

    data class User(
        val username: String,
        val id: Int
    )

    data class Errors(
        //val name: List<String>?,
        //val username: List<String>?,
        val email: List<String>?,
        val password: List<String>?,
        //val phone: List<String>?,
        //val password_confirmation: List<String>?
    )
}
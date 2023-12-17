package com.vearad.vearatick.model

data class UserResponse(
    val user: User

    ){
    data class User(
        val email: String,
        val id: Int,
        val is_admin: Int,
        val name: String,
        val phone: String,
        val username: String,
    )
}
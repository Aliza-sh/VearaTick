package com.vearad.vearatick.model.response

data class MiniSiteData (
    val business: Business

){
    data class Business(
        val namePer: String,
        val slug: String,
    )
}
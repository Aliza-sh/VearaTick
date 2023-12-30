package com.vearad.vearatick.model

data class MiniSiteData (
    val business: Business

){
    data class Business(
        val namePer: String,
        val slug: String,
    )
}
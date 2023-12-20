package com.vearad.vearatick.model

data class MiniSiteData (
    val miniSite: Business

){
    data class Business(
        val namePer: String,
        val slug: String,
    )
}
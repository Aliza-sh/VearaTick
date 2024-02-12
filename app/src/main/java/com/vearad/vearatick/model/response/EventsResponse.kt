package com.vearad.vearatick.model.response

data class EventsResponse(val events: List<Event>) {

    data class Event(
        val id: Int,
        val name: String,
        val slug: String,
        val attendanceCount: Int,
        val attendances: List<Attendance>,
        val by_user_id: Int,
        val capacity: Int?,
        val code: String?,
        val created_at: String,
        val end_date: String,
        val end_time: String,
        val formattedAddress: String,
        val geoCoordinate_lat: Double?,
        val geoCoordinate_lng: Double?,
        val image: Image,
        val organ: Organ,
        val organ_id: Int,
        val price: Double?,
        val start_date: String,
        val start_time: String,
        val status: String?,
        val type_id: Int,
        val updated_at: String
    )

    data class Attendance(
        val id: Int
    )

    data class Image(
        val id: Int,
        val path: String,
        val event_id: Int
        // Add other properties if necessary
    )

    data class Organ(
        val id: Int,
        val organ_head: String,
        val organ_head_phone: String
        // Add other properties if necessary
    )
}

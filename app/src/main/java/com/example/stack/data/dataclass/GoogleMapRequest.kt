package com.example.stack.data.dataclass

import com.squareup.moshi.Json

data class DistanceMatrixResponse(
    @Json(name = "destination_addresses") val destinationAddresses: List<String>,
    @Json(name = "origin_addresses") val originAddresses: List<String>,
    val rows: List<Row>,
    val status: String
) {
    data class Row(
        val elements: List<Element>
    )

    data class Element(
        val distance: TextValue?,
        val duration: TextValue?,
        @Json(name = "duration_in_traffic") val durationInTraffic: TextValue?,
        val status: String
    )

    data class TextValue(
        val text: String,
        val value: Int
    )
}
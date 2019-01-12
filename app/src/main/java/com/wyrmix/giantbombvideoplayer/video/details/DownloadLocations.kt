package com.wyrmix.giantbombvideoplayer.video.details

/**
 * Created by kylea
 *
 * 1/10/2019 at 7:28 PM
 */
enum class DownloadLocations(val value: Int) {
    Private(0), Movies(1), Downloads(2);

    companion object {
        private val map = DownloadLocations.values().associateBy(DownloadLocations::value)
        fun fromInt(type: Int) = map[type]
        fun parse(string: String?): DownloadLocations? {
            val sanitized = (string ?: "").trim().toLowerCase()
            return when(sanitized) {
                "private" -> Private
                "movies" -> Movies
                "downloads" -> Downloads
                else -> null
            }
        }
    }
}

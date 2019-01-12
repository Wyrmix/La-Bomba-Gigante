package com.wyrmix.giantbombvideoplayer.video.details

/**
 * Created by kylea
 *
 * 1/10/2019 at 11:21 AM
 */
enum class VideoQuality(val value: Int) {
    Low(0), High(1), HD(2), YouTube(3);

    companion object {
        private val map = VideoQuality.values().associateBy(VideoQuality::value)
        fun fromInt(type: Int) = map[type]
    }
}

package com.wyrmix.giantbombvideoplayer.video.models

/**
 * Created by kylea
 *
 * 10/15/2018 at 5:51 PM
 */
interface VideoType {
    val title: String
    val id: Long
    val image: Image

    fun filter(): String
}
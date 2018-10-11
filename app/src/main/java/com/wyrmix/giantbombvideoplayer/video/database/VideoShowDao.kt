package com.wyrmix.giantbombvideoplayer.video.database

import androidx.room.*

@Dao interface VideoShowDao {
    @Query("SELECT * FROM video_show")
    fun selectAll(): List<VideoShow>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoShow(vararg videoShow: VideoShow): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVideoShow(vararg videoShow: VideoShow): Int

    @Delete
    fun deleteVideoShow(videoShow: VideoShow)
}
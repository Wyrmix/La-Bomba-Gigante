package com.wyrmix.giantbombvideoplayer.video.database

import androidx.room.*

@Dao interface VideoCategoryDao {
    @Query("SELECT * FROM video_category")
    fun selectAll(): List<VideoCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoCategory(vararg videoCategory: VideoCategory): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVideoCategory(vararg videoCategory: VideoCategory): Int

    @Delete
    fun deleteVideoCategory(videoCategory: VideoCategory)
}
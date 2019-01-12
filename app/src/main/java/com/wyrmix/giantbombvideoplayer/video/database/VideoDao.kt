package com.wyrmix.giantbombvideoplayer.video.database

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface VideoDao {
    @Query("SELECT * FROM video")
    fun selectAll(): List<Video>

    @Query("SELECT * FROM video")
    fun selectPaged(): DataSource.Factory<Int, Video>

    @Query("SELECT * FROM video WHERE video.id is :id")
    fun getVideoById(id: Long): Video

    @Query("SELECT COUNT(*) FROM video")
    fun getNumberOfRows(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideo(vararg video: Video): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateVideo(vararg video: Video): Int

    @Delete
    fun deleteVideo(video: Video)
}
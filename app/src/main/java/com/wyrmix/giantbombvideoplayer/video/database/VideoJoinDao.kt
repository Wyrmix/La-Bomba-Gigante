package com.wyrmix.giantbombvideoplayer.video.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoJoinDao {
    @Query("SELECT * FROM video_join")
    fun selectAll(): List<VideoJoin>

    @Query("""
        SELECT * FROM video
        INNER JOIN video_join ON video.id=video_join.video_id
        WHERE video_join.video_show_id = :showId
    """)
    fun selectVideosForShow(showId: Int): List<Video>

    @Query("""
        SELECT * FROM video
        INNER JOIN video_join ON video.id=video_join.video_id
        WHERE video_join.video_category_id = :categoryId
    """)
    fun selectVideosForCategory(categoryId: Int): List<Video>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(videoJoin: VideoJoin)
}

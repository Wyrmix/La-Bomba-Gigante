package com.wyrmix.giantbombvideoplayer.video.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [
            VideoJoin::class,
            Video::class,
            VideoShow::class,
            VideoCategory::class
        ],
        version = 7,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao() : VideoDao
    abstract fun videoJoinDao(): VideoJoinDao
    abstract fun videoShowDao(): VideoShowDao
    abstract fun videoCategoryDao(): VideoCategoryDao
}
package com.wyrmix.giantbombvideoplayer.video.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
        entities = [
            Video::class,
            VideoShow::class,
            VideoCategory::class
        ],
        version = 8,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao() : VideoDao
    abstract fun videoShowDao(): VideoShowDao
    abstract fun videoCategoryDao(): VideoCategoryDao
}
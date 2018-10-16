package com.wyrmix.giantbombvideoplayer.video.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "video_join",
        foreignKeys = [
            ForeignKey(entity = Video::class, parentColumns = ["id"], childColumns = ["video_id"]),
            ForeignKey(entity = VideoShow::class, parentColumns = ["id"], childColumns = ["video_show_id"]),
            ForeignKey(entity = VideoCategory::class, parentColumns = ["id"], childColumns = ["video_category_id"])
        ]
)
data class VideoJoin(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var videoJoinId: Long = 0,
        @ColumnInfo(name = "video_id") var videoId: Long?,
        @ColumnInfo(name = "video_show_id") var videoShowId: Long?,
        @ColumnInfo(name = "video_category_id") var videoCategoryId: Long?
): Parcelable

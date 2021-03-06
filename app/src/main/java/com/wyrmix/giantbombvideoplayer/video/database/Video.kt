package com.wyrmix.giantbombvideoplayer.video.database

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.wyrmix.giantbombvideoplayer.video.models.Image
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "video", indices = [ Index(name = "video_id", value = ["id"]) ])
data class Video(
        @PrimaryKey @SerializedName("id") var id: Long = 0,
        @ColumnInfo(name = "api_detail_url") @SerializedName("api_detail_url") var apiDetailurl: String? = "",
        @ColumnInfo(name = "deck") @SerializedName("deck") var deck: String? = "",
        @ColumnInfo(name = "hd_url") @SerializedName("hd_url") var hdUrl: String? = "",
        @ColumnInfo(name = "high_url") @SerializedName("high_url") var highUrl: String? = "",
        @ColumnInfo(name = "low_url") @SerializedName("low_url") var lowUrl: String? = "",
        @ColumnInfo(name = "length_seconds") @SerializedName("length_seconds") var lengthSeconds: Int = 0,
        @ColumnInfo(name = "name") @SerializedName("name") var name: String? = "",
        @ColumnInfo(name = "publish_date") @SerializedName("publish_date") var publishDate: String? = "",
        @ColumnInfo(name = "site_detail_url") @SerializedName("site_detail_url") var siteDetailUrl: String? = "",
        @ColumnInfo(name = "url") @SerializedName("url") var ulr: String? = "",
        @Embedded @SerializedName("image") var videoImage: Image = Image(),
        @ColumnInfo(name = "user") @SerializedName("user") var user: String? = "",
        @ColumnInfo(name = "video_type") @SerializedName("video_type") var videoType: String? = "",
        @Ignore @SerializedName("video_show") var videoShow: VideoShow? = VideoShow(),
        @ColumnInfo(name = "shows") var shows: Long = videoShow?.id ?: 0,
        @Ignore @SerializedName("video_categories") var videoCategories: Set<VideoCategory?> = emptySet(),
        @ColumnInfo(name = "categories") var categories: String = videoCategories.joinToString(transform = { videoCategory -> videoCategory?.id.toString() }),
        @ColumnInfo(name = "youtube_id") @SerializedName("youtube_id") var youtubeId: String? = "",
        @ColumnInfo(name = "saved_time") @SerializedName("saved_time") var savedTime: Float = 0.0f
): Parcelable

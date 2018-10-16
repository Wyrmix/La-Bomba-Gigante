package com.wyrmix.giantbombvideoplayer.video.database

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.wyrmix.giantbombvideoplayer.video.models.Image
import com.wyrmix.giantbombvideoplayer.video.models.VideoType
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "video_category", indices = [ Index(name = "category_id", value = ["id"]) ])
data class VideoCategory (
        @PrimaryKey @SerializedName("id") override var id: Long = 0,
        @ColumnInfo(name = "api_detail_url") @SerializedName("api_detail_url") var apiDetailUrl: String = "",
        @ColumnInfo(name = "name") @SerializedName("name") override var title: String = "",
        @ColumnInfo(name = "deck") @SerializedName("deck") var deck: String = "",
        @ColumnInfo(name = "site_detail_url") @SerializedName("site_detail_url") var siteDetailUrl: String = "",
        @Embedded @SerializedName("image") override var image: Image = Image()
): Parcelable, VideoType {
    override fun filter(): String = "video_category:$id"
}

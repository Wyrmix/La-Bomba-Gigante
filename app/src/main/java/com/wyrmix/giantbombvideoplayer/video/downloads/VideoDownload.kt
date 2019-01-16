package com.wyrmix.giantbombvideoplayer.video.downloads

import com.tonyodev.fetch2.Download
import com.wyrmix.giantbombvideoplayer.video.database.Video

/**
 * Created by kylea
 *
 * 1/11/2019 at 6:04 PM
 */
data class VideoDownload(val video: Video, val download: Download?)
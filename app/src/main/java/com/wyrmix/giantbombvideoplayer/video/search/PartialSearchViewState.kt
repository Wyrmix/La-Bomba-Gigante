package com.wyrmix.giantbombvideoplayer.video.search

import com.wyrmix.giantbombvideoplayer.video.database.Video

/**
 * Created by kylea
 *
 * 10/11/2018 at 5:38 PM
 */
sealed class PartialSearchViewState {

    /**
     * The user has clicked on the search box to give it focus
     */
    object ReceivingFocus : PartialSearchViewState()

    /**
     * The User has clicked outside of the search view
     */
    object LosingFocus : PartialSearchViewState()

    /**
     * We're fetching data from the server so we show some sort of progress indication to the user
     */
    object LoadingPredictions : PartialSearchViewState()

    /**
     * We have a list of predictions to show the user
     */
    data class LoadedPredictions(val predictions: List<Video>) : PartialSearchViewState()

    /**
     * Some error should be displayed
     */
    data class Error(val error: Throwable) : PartialSearchViewState()
}

package com.wyrmix.giantbombvideoplayer.video.search

/**
 * Created by kylea
 *
 * 10/11/2018 at 5:39 PM
 * An error that happened in the SearchView. This has optional fields for recording a [SearchViewState] or [PartialSearchViewState] so we can recreate the UI at the time of the crash
 */
class SearchViewStateError(val searchViewState: SearchViewState?, val partialSearchViewState: PartialSearchViewState?, message: String?, cause: Throwable?) : Throwable(message, cause) {
    override fun toString(): String {
        return "SearchViewState: $searchViewState\n\nPartialSearchViewState: $partialSearchViewState\n\n$message"
    }
}
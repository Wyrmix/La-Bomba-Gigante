package com.wyrmix.giantbombvideoplayer.video.search

import com.wyrmix.giantbombvideoplayer.video.database.Video

/**
 * Created by kylea
 *
 * 10/11/2018 at 5:37 PM
 */
sealed class SearchViewState {
    val errorMessage = "There was an error before view states could be reduced"

    /**
     * The user hasn't focused on the search box yet
     */
    data class Unfocused(val predictions: List<Video> = emptyList(), val loading: Boolean = false)  : SearchViewState()

    /**
     * Once the search box is in focus, start loading predictions and show them to the user
     */
    data class Focused(val predictions: List<Video> = emptyList(), val loading: Boolean = false) : SearchViewState()

    /**
     * Says that an error has occurred while searching
     */
    data class Error(val error: SearchViewStateError) : SearchViewState() {
        override fun toString(): String = error.toString()
    }
}

/**
 * Combines an instance of [SearchViewState] with a [PartialSearchViewState] to return the final state for the view to render
 */
fun SearchViewState.reduce(partialChanges : PartialSearchViewState) : SearchViewState = when(this) {
    is SearchViewState.Focused -> {
        when(partialChanges) {
            is PartialSearchViewState.ReceivingFocus -> this
            is PartialSearchViewState.LosingFocus -> SearchViewState.Unfocused(predictions = this.predictions, loading = this.loading)
            is PartialSearchViewState.LoadingPredictions -> this.copy(predictions = this.predictions, loading = true)
            is PartialSearchViewState.LoadedPredictions -> this.copy(predictions = partialChanges.predictions, loading = false)
            is PartialSearchViewState.Error -> SearchViewState.Error(error = SearchViewStateError(this, partialChanges, errorMessage, partialChanges.error))
        }
    }
    is SearchViewState.Unfocused -> {
        when(partialChanges) {
            is PartialSearchViewState.ReceivingFocus -> SearchViewState.Focused(predictions = this.predictions, loading = this.loading)
            is PartialSearchViewState.LosingFocus -> this
            is PartialSearchViewState.LoadingPredictions -> this.copy(predictions = this.predictions, loading = true)
            is PartialSearchViewState.LoadedPredictions -> this.copy(predictions = partialChanges.predictions, loading = false)
            is PartialSearchViewState.Error -> SearchViewState.Error(error = SearchViewStateError(this, partialChanges, errorMessage, partialChanges.error))
        }
    }
    is SearchViewState.Error -> {
        when(partialChanges) {
            is PartialSearchViewState.Error -> this.copy(error = SearchViewStateError(this, partialChanges, errorMessage, partialChanges.error))
            else -> this
        }
    }
}
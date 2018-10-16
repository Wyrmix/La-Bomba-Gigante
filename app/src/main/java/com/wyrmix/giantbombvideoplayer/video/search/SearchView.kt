package com.wyrmix.giantbombvideoplayer.video.search

import kotlinx.coroutines.experimental.channels.Channel

/**
 * Created by kylea
 *
 * 10/11/2018 at 5:36 PM
 */
interface SearchView {
    /**
     * The search intent
     *
     * @return An observable emitting search query text
     */
    fun searchIntent(): Channel<String>

    /**
     * emits when the the this view gains or loses focus
     */
    fun focusIntent(): Channel<Boolean>

    /**
     * Renders the viewState
     *
     * @param viewState The current viewState state that should be displayed
     */
    fun render(viewState: SearchViewState)
}
package com.wyrmix.giantbombvideoplayer.auth

import android.content.ClipboardManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.material.snackbar.Snackbar
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.databinding.FragmentAuthenticationBinding
import com.wyrmix.giantbombvideoplayer.extension.generateBitmapFromRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.Koin.Companion.logger
import timber.log.Timber


/**
 * Fragment that handles launching a Chrome tab to the Boxee auth endpoint
 *
 * Once a user navigates back to this page we'll get the item from their clipboard and save it
 */
class AuthenticationFragment: Fragment() {

    val authViewModel: AuthenticationViewModel by viewModel()
    var progressButton: CircularProgressButton? = null
    var margin: Int = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAuthenticationBinding.inflate(inflater, container, false)

        binding.textViewAuthLink.setOnClickListener {
            val url = getString(com.wyrmix.giantbombvideoplayer.R.string.auth_url)
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            builder.setToolbarColor(ContextCompat.getColor(context!!, com.wyrmix.giantbombvideoplayer.R.color.primaryColor))
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }

        progressButton = binding.buttonAuthenticaion
        binding.buttonAuthenticaion.setOnClickListener { _ ->
            binding.buttonAuthenticaion.startAnimation()

            var authCodeText = binding.editTextAuthCode.text.toString().trim { it <= ' ' }

            if (authCodeText.isBlank()) {
                val clipboard = getSystemService<ClipboardManager>(context!!, ClipboardManager::class.java)
                authCodeText = clipboard?.primaryClip?.getItemAt(0)?.coerceToText(context).toString().trim()
                logger.info(authCodeText)
            }

            displaySnackBarWithBottomMargin(Snackbar.make(binding.root, GETTING_YOUR_API_KEY, Snackbar.LENGTH_LONG), 0, margin)

            GlobalScope.launch(Dispatchers.Main) {
                val success = authViewModel.authenticate(authCodeText)
                if (success) {
                    displaySnackBarWithBottomMargin(Snackbar.make(binding.root, GOT_YOUR_API_KEY, Snackbar.LENGTH_LONG), 0, margin)
                    context?.generateBitmapFromRes(R.drawable.ic_check)?.apply {
                        binding.buttonAuthenticaion.doneLoadingAnimation(R.color.success, this)
                    }
                } else {
                    displaySnackBarWithBottomMargin(Snackbar.make(binding.root, FAILED_TO_RETRIEVE_API_KEY, Snackbar.LENGTH_LONG), 0, margin)
                    context?.generateBitmapFromRes(R.drawable.ic_close)?.apply {
                        binding.buttonAuthenticaion.text = "Error - Try Again?"
                        binding.buttonAuthenticaion.doneLoadingAnimation(R.color.failure, this)
                    }
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        progressButton?.dispose()
    }

    private fun displaySnackBarWithBottomMargin(snackbar: Snackbar, sideMargin: Int, marginBottom: Int) {
        val snackBarView = snackbar.view
        val params = snackBarView.layoutParams as ViewGroup.MarginLayoutParams

        params.setMargins(params.leftMargin + sideMargin,
                params.topMargin,
                params.rightMargin + sideMargin,
                params.bottomMargin + marginBottom)
        Timber.i("params [${params.bottomMargin}]")

        snackBarView.layoutParams = params
        snackbar.show()
    }
}

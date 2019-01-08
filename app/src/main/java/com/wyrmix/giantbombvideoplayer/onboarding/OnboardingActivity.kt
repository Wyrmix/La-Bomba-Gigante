package com.wyrmix.giantbombvideoplayer.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.wyrmix.giantbombvideoplayer.R
import com.wyrmix.giantbombvideoplayer.REQUEST_CODE_ONBOARDING
import com.wyrmix.giantbombvideoplayer.auth.AuthenticationFragment
import timber.log.Timber


class OnboardingActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val explanationPage = SliderPage()
        explanationPage.title = "Video App"
        explanationPage.description = "This is a video browser for giantbomb.com"
        explanationPage.imageDrawable = R.drawable.ic_video_intro
        explanationPage.bgColor = ContextCompat.getColor(this, R.color.primaryLightColor)
        addSlide(AppIntroFragment.newInstance(explanationPage))

        val pipPage = SliderPage()
        pipPage.title = "Picture in Picture"
        pipPage.description = "This app features Picture in Picture on Android Oreo and above"
        pipPage.imageDrawable = R.drawable.ic_pip
        pipPage.bgColor = ContextCompat.getColor(this, R.color.primaryLightColor)
        addSlide(AppIntroFragment.newInstance(pipPage))

        val authPage = SliderPage()
        authPage.title = "Boxee Auth"
        authPage.description = "Because of the way the giantbomb system works you must login to use the app. Both free and premium accounts are supported"
        authPage.imageDrawable = R.drawable.ic_acount
        authPage.bgColor = ContextCompat.getColor(this, R.color.primaryLightColor)
        addSlide(AppIntroFragment.newInstance(authPage))

        val authFrag = AuthenticationFragment()
        authFrag.margin = 192
        addSlide(authFrag)

        showSkipButton(false)
        isProgressButtonEnabled = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        val data = Intent()
        data.putExtra(getString(R.string.user_has_completed_onboarding), true)
        setResult(REQUEST_CODE_ONBOARDING, data)
        finish()
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        Timber.d("oldFragment: [$oldFragment], newFragment: [$newFragment]")
    }
}

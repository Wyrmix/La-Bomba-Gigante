package com.wyrmix.giantbombvideoplayer

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.util.LruCache
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.wyrmix.giantbombvideoplayer.databinding.ActivityMainBinding
import com.wyrmix.giantbombvideoplayer.extension.navigateUp
import com.wyrmix.giantbombvideoplayer.video.list.VideoBrowseViewModel
import com.wyrmix.giantbombvideoplayer.video.models.VideoType
import com.wyrmix.giantbombvideoplayer.video.network.ApiRepository
import io.palaima.debugdrawer.DebugDrawer
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.IO
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.async
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

/**
 * garbage activity to launch into different parts of the app during development
 */
class MainActivity : AppCompatActivity() {
    val apiRepository by inject<ApiRepository>()
    val sharedPrefs by inject<SharedPreferences>()
    val viewModel by viewModel<VideoBrowseViewModel>()
    val memCache by inject<LruCache<String, Bitmap>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.i("viewModel $viewModel")

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(R.id.main_nav_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavBar, navController)

        // Set up ActionBar
        setSupportActionBar(binding.toolbar)
        NavigationUI.setupActionBarWithNavController(this, navController)

        setupActionBarWithNavController(navController)

        viewModel.videoTypes.observe(this, Observer {
            Timber.i("viewModel livedata returned $it")
            preloadImages(it)
        })
        viewModel.fetchCategoriesAndShows()

        get<DebugDrawer> { parametersOf(this) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_browse, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Timber.i("context item selected $item")
        if (item == null) return false

        when (item.itemId) {
            R.id.action_search -> search()
            R.id.action_filter -> filter()
        }

        return true
    }

    fun search() {
        Timber.d("search")
    }

    fun filter() {
        Timber.d("filter")


        GlobalScope.async(Dispatchers.Main) {
            val layout = LayoutInflater.from(this@MainActivity).inflate(R.layout.dialog_filter, findViewById(android.R.id.content), false)
            val chipGroup = layout.findViewById<ChipGroup>(R.id.chipGroup) ?: return@async

            val dialog = AlertDialog.Builder(this@MainActivity)
                    .setView(layout)
                    .setPositiveButton("Confirm") { dialog, which ->
                        Timber.i("clicked positive button")
                        val set = mutableSetOf<String>()
                        chipGroup.children.forEach {
                            val chip = it as? Chip ?: return@forEach
                            if (chip.isChecked) set.add(chip.tag as String)
                        }
                        val editor = sharedPrefs.edit()
                        editor.putStringSet("filter", set)
                        editor.apply()
                    }
                    .setNegativeButton("Cancel", null)
                    .setTitle("Filter")
                    .show()

            val set = sharedPrefs.getStringSet("filter", emptySet()) ?: emptySet()

            viewModel.videoTypes.value?.forEach {
                val chip = Chip(this@MainActivity)
                chip.text = it.title
                chip.isCheckable = true
                chip.isChecked = set.contains(it.filter())

                val image = memCache.get(it.title) ?: async(Dispatchers.IO) {
                    Glide.with(this@MainActivity)
                            .asBitmap()
                            .load(it.image.tinyImageUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .submit()
                            .get()
                }.await()
                chip.chipIcon = BitmapDrawable(resources, image)
                chip.tag = it.filter()
                chipGroup.addView(chip)
            }
        }
    }

    private fun preloadImages(types: List<VideoType>) {
        val windowManager = getSystemService(WindowManager::class.java)
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x //width of screen in pixels
        val height = size.y//height of screen in pixels

        types.forEach {
            Glide.with(this)
                    .asBitmap()
                    .load(it.image.smallImageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(object : SimpleTarget<Bitmap>(width, height) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            memCache.put(it.title, resource)
                        }
                    })
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(findNavController(R.id.main_nav_fragment))
    }
}

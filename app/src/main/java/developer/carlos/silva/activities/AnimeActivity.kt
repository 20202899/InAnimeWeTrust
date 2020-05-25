package developer.carlos.silva.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import developer.carlos.silva.R
import developer.carlos.silva.adapters.EpisodeAdapter
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.content_anime.*


class AnimeActivity : AppCompatActivity(), AnimeLoaderListener {

    private val mAdapter = EpisodeAdapter()
    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        commomInit()
    }

    fun commomInit() {
        val anime = intent.extras["data"] as Anime

        mSharedPreferences = getSharedPreferences("Sets", Context.MODE_PRIVATE)

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        mAdapter.mActivity = this

        mAdapter.IMG_PATH = anime.imagePath

        recyclerview.adapter = mAdapter

        title = anime.title

        LoaderAnimes.loadAnimes(this, anime.link)
    }

    fun createPalette(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->

            val dominantSwatch = palette?.dominantSwatch
            val vibrantSwatch = palette?.vibrantSwatch

            mAdapter.RGB_COLOR_DOMINANT = dominantSwatch?.rgb ?: ContextCompat.getColor(
                this,
                R.color.colorPrimary
            )

            mAdapter.RGB_COLOR_TEXT = ContextCompat.getColor(this, android.R.color.holo_red_dark)

            toolbar_layout.setContentScrimColor(
                dominantSwatch?.rgb ?: ContextCompat.getColor(this, R.color.colorPrimary)
            )

            toolbar.navigationIcon?.setColorFilter(
                dominantSwatch?.titleTextColor ?: ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_ATOP
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor =
                    dominantSwatch?.rgb ?: ContextCompat.getColor(this, R.color.colorPrimary)
            }

            with(app_bar) {
                setBackgroundColor(
                    dominantSwatch?.rgb ?: ContextCompat.getColor(context, R.color.colorPrimary)
                )
                toolbar.setTitleTextColor(
                    dominantSwatch?.titleTextColor ?: ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home)
            finishAfterTransition()

        return super.onOptionsItemSelected(item)
    }

    override fun onLoad(animes: MutableList<Any>) {
        mAdapter.addItems(animes)
    }

    fun saveSets(id: String) {
        val saveSets = mSharedPreferences.getStringSet("sets", mutableSetOf())

        if (saveSets.find { it == id } == null) {
            val edit = mSharedPreferences.edit()
            saveSets.add(id)
            edit.clear()
            edit.putStringSet("sets", saveSets)
            edit.apply()
        }
    }

    fun getIds(): MutableList<Int> {
        val saveSets = mSharedPreferences.getStringSet("sets", mutableSetOf())
        return saveSets.map { it.toInt() }.toMutableList()
    }
}

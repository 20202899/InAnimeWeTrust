package developer.carlos.silva.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import developer.carlos.silva.R
import developer.carlos.silva.adapters.EpisodeAdapter
import developer.carlos.silva.database.DatabaseServices
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.extensions.addAnim
import developer.carlos.silva.extensions.removeAnim
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.singletons.MainController
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.content_anime.*


class AnimeActivity : AppCompatActivity(), AnimeLoaderListener {

    private val mAdapter = EpisodeAdapter()
    private lateinit var mSharedPreferences: SharedPreferences
    var isLink = false
    private lateinit var dataAnime: DataAnime
    private val mGson = Gson()

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

        fab.setOnClickListener {
            Thread {
                val db = DatabaseServices.getDataBaseInstance(this)
                val daoAnime = db.dataAnimeDao()

                if (daoAnime.isExist(dataAnime.id) == null) {
                    dataAnime.title = anime.title
                    dataAnime.link = anime.link
                    daoAnime.insertAnime(dataAnime)
                    daoAnime.insertEpisode(dataAnime.lista)
                    MainController.getInstance()?.getHandler()?.post {
                        fab.removeAnim()
                    }
                } else {
                    daoAnime.deleteById(dataAnime.id)
                    MainController.getInstance()?.getHandler()?.post {
                        fab.addAnim()
                    }
                }

                db.close()
            }.start()
        }

        mAdapter.RGB_COLOR_TEXT = ContextCompat.getColor(this, android.R.color.holo_red_dark)

        mAdapter.mActivity = this

        isLink = anime.imagePath.isNotEmpty()

//        mAdapter.IMG_PATH = anime.imagePath

        recyclerview.adapter = mAdapter

        title = anime.title

        LoaderAnimes.loadAnimes(this, anime.link)
    }

    fun isCheckAdded() {

        if (fab.visibility == FloatingActionButton.GONE) {
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_fade_in))
            fab.visibility = FloatingActionButton.VISIBLE
        }

        Thread {
            val db = DatabaseServices.getDataBaseInstance(this)
            val daoAnime = db.dataAnimeDao()

            if (daoAnime.isExist(dataAnime.id) != null) {
                MainController.getInstance()?.getHandler()?.post {
                    fab.removeAnim()
                }
            } else {
                daoAnime.deleteById(dataAnime.id)
                MainController.getInstance()?.getHandler()?.post {
                    fab.addAnim()
                }
            }
            db.close()
        }.start()
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

            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor =
                dominantSwatch?.rgb ?: ContextCompat.getColor(this, R.color.colorPrimary)

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

            if (!isLink)
                mAdapter.notifyDataSetChanged()

        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home)
            finishAfterTransition()

        return super.onOptionsItemSelected(item)
    }

    override fun onLoad(data: MutableList<Any>) {
        dataAnime = data[0] as DataAnime

        isCheckAdded()

        if (mAdapter.IMG_PATH.isNullOrEmpty()) {
            mAdapter.IMG_PATH = dataAnime.capa
        }

        mAdapter.addItems(dataAnime.lista as MutableList<Any>)

        progressbar.visibility = ProgressBar.GONE
        recyclerview.visibility = RecyclerView.VISIBLE
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

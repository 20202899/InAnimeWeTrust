package developer.carlos.silva.activities

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import developer.carlos.silva.R
import developer.carlos.silva.adapters.EpisodeAdapter
import developer.carlos.silva.database.DatabaseServices
import developer.carlos.silva.database.models.AnimeAndEpisodes
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.extensions.addAnim
import developer.carlos.silva.extensions.removeAnim
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.singletons.MainController
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.content_anime.*
import kotlinx.android.synthetic.main.content_anime.recyclerview
import java.lang.Exception


class AnimeActivity : AppCompatActivity(), AnimeLoaderListener {

    private val mAdapter = EpisodeAdapter()
    private lateinit var mSharedPreferences: SharedPreferences
    var isLink = false
    private var dataAnime: DataAnime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportPostponeEnterTransition()
        commomInit()
    }

    fun finishTransition () {
        supportStartPostponedEnterTransition()
    }

    fun commomInit() {
        mSharedPreferences = getSharedPreferences("Sets", Context.MODE_PRIVATE)

        start_watch.setOnClickListener {

            if (mAdapter.isContinue()) {
                app_bar.setExpanded(false, true)
                mAdapter.startOrContinue()
            }
//                else {
//                    newHolder.start_watch.background =
//                        ContextCompat.getDrawable(mActivity, R.drawable.start_watch_selected)
//                    newHolder.start_watch.text = "Continuar"
//                }

        }

        val data = intent.extras["data"]
        var listener: View.OnClickListener? = null
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL, false
        )

        recyclerview.adapter = mAdapter

        mAdapter.mActivity = this
        if (data is Anime) {

            isLink = data.imagePath.isNotEmpty()
            title = data.title

            LoaderAnimes.loadAnimes(this, data.link)

            listener = View.OnClickListener {
                Thread {
                    val db = DatabaseServices.getDataBaseInstance(this)
                    val daoAnime = db.dataAnimeDao()

                    if (daoAnime.isExist(dataAnime!!.id) == null) {
                        dataAnime!!.title = data.title
                        dataAnime!!.link = data.link
                        daoAnime.insertAnime(dataAnime!!)
                        daoAnime.insertEpisode(dataAnime!!.lista)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.removeAnim()
                        }
                    } else {
                        daoAnime.deleteById(dataAnime!!.id)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.addAnim()
                        }
                    }

                    db.close()
                }.start()
            }
        }

        if (data is AnimeAndEpisodes) {
            dataAnime = data.dataAnime
            isLink = dataAnime!!.capa.isNotEmpty()
            title = dataAnime!!.title
            mAdapter.IMG_PATH = dataAnime!!.capa
            mAdapter.mDataAnime = dataAnime
            text2.text = dataAnime?.sinopse
            Picasso.get()
                .load(mAdapter.IMG_PATH)
                .fit()
                .noFade()
                .into(img, object : Callback {
                    override fun onSuccess() {
                        finishTransition()
                    }

                    override fun onError(e: Exception?) {
                        finishTransition()
                    }

                })
            isCheckAdded()
            listener = View.OnClickListener {
                Thread {
                    val db = DatabaseServices.getDataBaseInstance(this)
                    val daoAnime = db.dataAnimeDao()

                    if (daoAnime.isExist(dataAnime!!.id) == null) {
                        daoAnime.insertAnime(dataAnime!!)
                        daoAnime.insertEpisode(data.epsodes)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.removeAnim()
                        }
                    } else {
                        daoAnime.deleteById(dataAnime!!.id)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.addAnim()
                        }
                    }

                    db.close()
                }.start()
            }

            val eps = data.epsodes
            eps.sortBy {
                it.order
            }
            mAdapter.addItems(eps.toMutableList())

            progressbar.visibility = ProgressBar.GONE
            recyclerview.visibility = RecyclerView.VISIBLE
        }

        fab.setOnClickListener(listener)
        mAdapter.RGB_COLOR_TEXT = ContextCompat.getColor(this, android.R.color.holo_red_dark)
    }

    fun isCheckAdded() {

        if (fab.visibility == FloatingActionButton.GONE) {
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_fade_in))
            fab.visibility = FloatingActionButton.VISIBLE
        }

        Thread {
            val db = DatabaseServices.getDataBaseInstance(this)
            val daoAnime = db.dataAnimeDao()

            if (daoAnime.isExist(dataAnime!!.id) != null) {
                MainController.getInstance()?.getHandler()?.post {
                    fab.removeAnim()
                }
            } else {
                daoAnime.deleteById(dataAnime!!.id)
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

            mAdapter.RGB_COLOR_TEXT =
                ContextCompat.getColor(this, android.R.color.holo_red_dark)

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

        if (data.size <= 0)
            return

        dataAnime = data[0] as DataAnime
        dataAnime?.title = title.toString()
        mAdapter.mDataAnime = dataAnime

        isCheckAdded()

        if (mAdapter.IMG_PATH.isNullOrEmpty()) {
            mAdapter.IMG_PATH = dataAnime!!.capa
            text2.text = dataAnime?.sinopse
            Picasso.get()
                .load(mAdapter.IMG_PATH)
                .fit()
                .noFade()
                .into(img, object : Callback {
                    override fun onSuccess() {
                        finishTransition()
                    }

                    override fun onError(e: Exception?) {
                        finishTransition()
                    }

                })
        }

        mAdapter.addItems(dataAnime!!.lista.toMutableList())

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

package developer.carlos.silva.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette

import androidx.recyclerview.widget.LinearLayoutManager
import android.transition.ChangeBounds
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import developer.carlos.silva.R
import developer.carlos.silva.adapters.EpisodeAdapter
import developer.carlos.silva.database.DatabaseServices
import developer.carlos.silva.database.models.AnimeAndEpisodes
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.extensions.hideSystemUI
import developer.carlos.silva.extensions.showSystemUI
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.content_anime.recyclerview
import kotlinx.android.synthetic.main.fab_expand_layout.view.*
import java.lang.Exception


class AnimeActivity : AppCompatActivity(), AnimeLoaderListener {

    private val mAdapter = EpisodeAdapter()
    private lateinit var mSharedPreferences: SharedPreferences
    var isLink = false
    private var dataAnime: DataAnime? = null
    var defaultHeightAppBar = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anime)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportPostponeEnterTransition()
        commomInit()


        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            hideSystemUI()
        }else {
            showSystemUI()
        }

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
        fab.mActivity = this
        if (data is Anime) {

            isLink = data.imagePath.isNotEmpty()
            title = data.title
            fab.title = data.title
            mAdapter.IMG_PATH = data.imagePath
            LoaderAnimes.loadAnimes(this, data.link)

            listener = View.OnClickListener {
                Thread {
                    val db = DatabaseServices.getDataBaseInstance(this)
                    val daoAnime = db.dataAnimeDao()

                    if (daoAnime.isExist(dataAnime!!.id) == null) {
                        dataAnime!!.isWatch = fab.isWatching
                        dataAnime!!.notifyMe = fab.notifyMe
                        dataAnime!!.title = data.title
                        dataAnime!!.link = data.link
                        daoAnime.insertAnime(dataAnime!!)
                        daoAnime.insertEpisodes(dataAnime!!.lista)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.isAdded = true
                        }
                    } else {
                        daoAnime.deleteById(dataAnime!!.id)
                        MainController.getInstance()?.getHandler()?.post {
                            fab.isAdded = false
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
            fab.title = dataAnime!!.title

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

            listener = View.OnClickListener {
                Thread {
                    val db = DatabaseServices.getDataBaseInstance(this)
                    val daoAnime = db.dataAnimeDao()

                    if (daoAnime.isExist(dataAnime!!.id) == null) {
                        dataAnime!!.isWatch = fab.isWatching
                        dataAnime!!.notifyMe = fab.notifyMe
                        daoAnime.insertAnime(dataAnime!!)
                        daoAnime.insertEpisodes(data.epsodes)
                        MainController.getInstance()?.getHandler()?.post {
                            val changeBounds = ChangeBounds()
                            window.sharedElementReturnTransition = changeBounds
                            window.sharedElementReenterTransition = changeBounds
                            window.sharedElementExitTransition = changeBounds
                            img.transitionName = getString(R.string.transition_name)
                            fab.isAdded = true
                        }
                    } else {
                        daoAnime.deleteById(dataAnime!!.id)
                        MainController.getInstance()?.getHandler()?.post {
                            window.sharedElementReturnTransition = null
                            window.sharedElementReenterTransition = null
                            window.sharedElementExitTransition = null
                            img.transitionName = null
                            fab.isAdded = false
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

            isCheckAdded()

        }

        fab.setOnAddClickListener(listener)
        fab.onClickListener(View.OnClickListener {
            fab.checkbox.isChecked = false
            isCheckAdded()
        })
        mAdapter.RGB_COLOR_TEXT = ContextCompat.getColor(this, android.R.color.holo_red_dark)
        defaultHeightAppBar = app_bar.layoutParams.height
    }

    private fun isCheckAdded() {

        if (fab.visibility == FloatingActionButton.GONE) {
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_fade_in))
            fab.visibility = FloatingActionButton.VISIBLE
        }

        Thread {
            val db = DatabaseServices.getDataBaseInstance(this)
            val daoAnime = db.dataAnimeDao()

            if (daoAnime.isExist(dataAnime!!.id) != null) {
                MainController.getInstance()?.getHandler()?.post {
                    fab.isAdded = true
                    fab.removeAnim()
                }
            } else {
                daoAnime.deleteById(dataAnime!!.id)
                MainController.getInstance()?.getHandler()?.post {
                    fab.isAdded = false
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
            onBackPressed()

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
        }else {
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

        dataAnime?.infos?.forEach { i ->
            infos.addView(TextView(this@AnimeActivity).apply {
                val margin = Utils.dpToPx(8f, resources).toInt()
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                setPadding(Utils.dpToPx(15f, resources).toInt())
                params.setMargins(margin, 0, margin, margin)
                text = i
                textSize = 16f
                setTextAppearance(android.R.style.TextAppearance_StatusBar_Title)
                layoutParams = params
                background = ContextCompat.getDrawable(this@AnimeActivity, R.drawable.tag_background)
                setTextColor(ContextCompat.getColor(this@AnimeActivity, android.R.color.white))
            })
        }

        mAdapter.addItems(dataAnime!!.lista.toMutableList())

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

package developer.carlos.silva.activities

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import developer.carlos.silva.R
import developer.carlos.silva.adapters.FragmentAdapter
import developer.carlos.silva.extensions.hideSystemUI
import developer.carlos.silva.extensions.show
import developer.carlos.silva.extensions.showSystemUI
import developer.carlos.silva.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mSharedPreferences: SharedPreferences
    lateinit var mFragmentAdapter: FragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mSharedPreferences = getSharedPreferences("Sets", Context.MODE_PRIVATE)

        bottomview.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home_id -> {
                    val chieldFM = mFragmentAdapter.fragments[0].childFragmentManager
                    if (chieldFM.findFragmentById(R.id.container_main) is EpisodesFragment || chieldFM.findFragmentById(
                            R.id.container_main
                        ) is CalendarFragment
                    ) {
                        val result = supportFragmentManager.fragments.last() is CalendarFragment

                        if (result) {
                            toolbar_layout.title = "Calendário"
                            title = "Calendário"
                        } else {
                            toolbar_layout.title = "Disponível"
                            title = "Disponível"
                        }

                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        toolbar.showWithAnimation()
                    } else if (mFragmentAdapter.fragments[2].childFragmentManager.findFragmentById(R.id.container_animes) != null) {
                        toolbar_layout.title = getString(R.string.app_name)
                        supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        toolbar.hideWithAnimation()
                    } else {
                        toolbar_layout.title = getString(R.string.app_name)
//                        title = getString(R.string.app_name)
                    }

                    viewpager.currentItem = 0
                    search_view.visibility = SearchView.GONE
                }

                R.id.list_id -> {

                    val fragment = mFragmentAdapter.fragments[2].childFragmentManager.findFragmentById(R.id.container_animes)

                    if (fragment == null) {
                        toolbar.hideWithAnimation()?.addListener(object : Animator.AnimatorListener {
                            override fun onAnimationRepeat(animation: Animator?) {

                            }

                            override fun onAnimationEnd(animation: Animator?) {
                                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                            }

                            override fun onAnimationCancel(animation: Animator?) {

                            }

                            override fun onAnimationStart(animation: Animator?) {

                            }

                        })


                        toolbar_layout.title = "Gêneros"
                    } else {
                        var title = "Gêneros"

                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        toolbar.showWithAnimation()
                        (fragment as AnimesFragment?)?.apply {
                            title = arguments!!.getString("title") ?: "Gêneros"
                        }

                        toolbar_layout.title = title
                    }

                    search_view.visibility = SearchView.GONE
                    viewpager.currentItem = 2
                }

                R.id.search_id -> {
                    toolbar.hideWithAnimation()?.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }

                    })
                    viewpager.currentItem = 1
                    app_bar.setExpanded(true, true)
                    search_view.show()
                    toolbar_layout.title = ""
                    title = ""
                }

                R.id.favorite_id -> {

                    val intent = Intent(this, FavoriteActivity::class.java)
                    val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this)
                        .toBundle()

                    startActivityForResult(intent, Companion.REQUEST_RESULT, bundle)
                }
            }

            toolbar_layout.collapsedTitleGravity = Gravity.CENTER
            toolbar_layout.expandedTitleGravity = Gravity.CENTER

            return@setOnNavigationItemSelectedListener true
        }

        mFragmentAdapter = FragmentAdapter(supportFragmentManager)
        mFragmentAdapter.fragments.addAll(
            mutableListOf(
                HomeFragment.newInstance(),
                SearchFragment.newInstance(),
                GenreFragment.newInstance()
            )
        )

        viewpager.offscreenPageLimit = 3
        viewpager.adapter = mFragmentAdapter

        if (requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            hideSystemUI()
        } else {
            showSystemUI()
        }


        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            getSharedPreferences("Try", Context.MODE_PRIVATE)
                .edit()
                .putString("error", throwable.message)
                .apply()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> {
                val chieldFM = mFragmentAdapter.fragments[0].childFragmentManager
                val fragment = chieldFM.findFragmentById(R.id.container_main)
                if (fragment != null && viewpager.currentItem == 0
                ) {
                    toolbar_layout.title = getString(R.string.app_name)
//                    supportFragmentManager.beginTransaction().remove(fragment).commit()
                    chieldFM.popBackStack()
                    toolbar.hideWithAnimation()?.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }

                    })
                } else {
                    toolbar_layout.title = "Gêneros"
                    mFragmentAdapter.fragments[2].childFragmentManager.popBackStack()
                    toolbar.hideWithAnimation()?.addListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {

                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        }

                        override fun onAnimationCancel(animation: Animator?) {

                        }

                        override fun onAnimationStart(animation: Animator?) {

                        }

                    })
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {

        (supportFragmentManager.findFragmentById(android.R.id.content) as PlayFragment?)?.apply {
            if (!isFullScreen) {
                supportFragmentManager.popBackStack()
                return
            }
        }

        val chieldFM = mFragmentAdapter.fragments[0].childFragmentManager
        if ((chieldFM.findFragmentById(R.id.container_main) !is EpisodesFragment || chieldFM.findFragmentById(
                R.id.container_main
            ) !is CalendarFragment) &&
            viewpager.currentItem == 0
        ) {
            toolbar_layout.title = getString(R.string.app_name)
            title = getString(R.string.app_name)
            toolbar.hideWithAnimation()?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })

            super.onBackPressed()

        } else if ((chieldFM.findFragmentById(R.id.container_main) !is EpisodesFragment || chieldFM.findFragmentById(
                R.id.container_main
            ) !is CalendarFragment)
            && viewpager.currentItem > 0
        ) {

            bottomview.selectedItemId = R.id.home_id
//            viewpager.currentItem = 0
        } else if ((chieldFM.findFragmentById(R.id.container_main) is EpisodesFragment || chieldFM.findFragmentById(
                R.id.container_main
            ) is CalendarFragment) &&
            viewpager.currentItem > 0
        ) {
            bottomview.selectedItemId = R.id.home_id
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_RESULT) {
            bottomview.selectedItemId = when (viewpager.currentItem) {
                0 -> {
                    R.id.home_id
                }
                1 -> {
                    R.id.search_id
                }
                else -> {
                    R.id.list_id
                }
            }
        }
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

    fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(this)
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
    }

    companion object {
        const val REQUEST_RESULT: Int = 32
    }

}

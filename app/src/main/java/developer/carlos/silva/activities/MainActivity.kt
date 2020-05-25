package developer.carlos.silva.activities

import android.animation.Animator
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import developer.carlos.silva.R
import developer.carlos.silva.adapters.FragmentAdapter
import developer.carlos.silva.extensions.hide
import developer.carlos.silva.extensions.show
import developer.carlos.silva.fragments.EpisodesFragment
import developer.carlos.silva.fragments.FavoriteFragment
import developer.carlos.silva.fragments.HomeFragment
import developer.carlos.silva.fragments.SearchFragment
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

                    if (supportFragmentManager.findFragmentById(R.id.container_main)
                                is EpisodesFragment
                    ) {
                        supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        toolbar.show()
                    }

                    viewpager.currentItem = 0
                    search_view.visibility = SearchView.GONE
                    toolbar_layout.isTitleEnabled = true
                    title = getString(R.string.app_name)
                }

                R.id.search_id -> {
                    toolbar.hide().addListener(object : Animator.AnimatorListener{
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
                    search_view.visibility = SearchView.VISIBLE
                    toolbar_layout.isTitleEnabled = true
                    toolbar_layout.title = ""
                    title = ""
                }

                R.id.favorite_id -> {

                    toolbar.hide().addListener(object : Animator.AnimatorListener{
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

                    search_view.visibility = SearchView.GONE
                    title = getString(R.string.app_name)
                    viewpager.currentItem = 2
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        mFragmentAdapter = FragmentAdapter(supportFragmentManager)
        mFragmentAdapter.fragments.addAll(
            mutableListOf(
                HomeFragment.newInstance(),
                SearchFragment.newInstance(),
                FavoriteFragment.newInstance()
            )
        )

        viewpager.offscreenPageLimit = 3
        viewpager.adapter = mFragmentAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> {
                supportFragmentManager.popBackStack()
                toolbar.hide().addListener(object : Animator.AnimatorListener{
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

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.container_main) !is EpisodesFragment &&
            viewpager.currentItem == 0) {
            super.onBackPressed()
        }else if (supportFragmentManager.findFragmentById(R.id.container_main) !is EpisodesFragment
            && viewpager.currentItem > 0) {
            bottomview.selectedItemId = R.id.home_id
            viewpager.currentItem = 0
        }else if(supportFragmentManager.findFragmentById(R.id.container_main) is EpisodesFragment &&
            viewpager.currentItem > 0) {
            bottomview.selectedItemId = R.id.home_id
            viewpager.currentItem = 0
        }else {
            supportFragmentManager.popBackStack()
            toolbar.hide().addListener(object : Animator.AnimatorListener{
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

}

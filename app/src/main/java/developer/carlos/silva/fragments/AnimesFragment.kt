package developer.carlos.silva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.adapters.SearchAdapter
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.interfaces.EndlessRecyclerViewScrollListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.episodes_fragment.*

class AnimesFragment : Fragment() {

    private val mSearchAdapter = SearchAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.animes_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview.setHasFixedSize(true)

        val mainActivity = activity as MainActivity

        mSearchAdapter.mMainActivity = mainActivity

        recyclerview.layoutManager =
            GridLayoutManager(context!!, 2, GridLayoutManager.VERTICAL, false)

        var next = arguments?.getString("link") ?: ""

        val endlessScrollListener =
            object : EndlessRecyclerViewScrollListener(recyclerview.layoutManager as GridLayoutManager) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    LoaderAnimes.loadAnimesByGenre(object : AnimeLoaderListener {
                        override fun onLoad(animes: MutableList<Any>) {
                            val a = animes[1] as MutableList<Anime>
                            mSearchAdapter.addAll(animes[0] as MutableList<Any>)
                            next = (a.last()).link
                        }
                    }, next)
                }
            }

        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.toolbar_layout.title = arguments?.getString("title")
        mainActivity.toolbar.showWithAnimation()

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mainActivity.bottomview.isShowing()) {
                    mainActivity.bottomview.hide()
                } else if (dy <= 0 && !mainActivity.bottomview.isShowing()) {
                    mainActivity.bottomview.show()
                }
            }
        })

        recyclerview.addOnScrollListener(endlessScrollListener)

        recyclerview.adapter = mSearchAdapter

        LoaderAnimes.loadAnimesByGenre(object : AnimeLoaderListener {
            override fun onLoad(animes: MutableList<Any>) {
                mSearchAdapter.addItems(animes[0] as MutableList<Any>)
                val a = animes[1] as MutableList<Anime>
                next = (a.last()).link
            }
        }, arguments?.getString("link") ?: "")
    }

    companion object {
        const val FRAGMENT_ID = "Genre"
        fun newInstance(title: String, link: String) = AnimesFragment().apply {
            arguments = Bundle().apply {
                putString("title", title)
                putString("link", link)
            }
        }
    }
}
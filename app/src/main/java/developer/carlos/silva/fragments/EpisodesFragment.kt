package developer.carlos.silva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.adapters.EpisodesAdapter
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.interfaces.EndlessRecyclerViewScrollListener
import developer.carlos.silva.models.Anime
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.network.UrlSystem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.episodes_fragment.recyclerview

class EpisodesFragment : Fragment() {

    val mAdapter = EpisodesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.episodes_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerview.setHasFixedSize(true)

        val mainActivity = activity as MainActivity
        val linearLayoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL,
            false
        )

        var next = UrlSystem.episodes()

        val endlessScrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                LoaderAnimes.pagination(object : AnimeLoaderListener {
                    override fun onLoad(links: MutableList<Any>) {
                        next = (links.last() as Anime).link
                        LoaderAnimes.loadEpisodes(object : AnimeLoaderListener {
                            override fun onLoad(animes: MutableList<Any>) {
                                mAdapter.addItems(animes)
                            }
                        }, next)
                    }
                }, next)
            }
        }

        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.toolbar_layout.title = "Disponível"
        mainActivity.toolbar.showWithAnimation()

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && mainActivity.bottomview.isShowing()) {
                    mainActivity.bottomview.hide()
                }else if(dy <= 0 && !mainActivity.bottomview.isShowing()) {
                    mainActivity.bottomview.show()
                }
            }
        })

        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = mAdapter
        recyclerview.addOnScrollListener(endlessScrollListener)

        mAdapter.mEpisodesFragment = this

        LoaderAnimes.loadEpisodes(object : AnimeLoaderListener {
            override fun onLoad(animes: MutableList<Any>) {
                mAdapter.addItems(animes)
            }
        })
    }

    companion object {
        const val FRAGMENT_ID = "Episodes"
        fun newInstance() = EpisodesFragment()
    }
}
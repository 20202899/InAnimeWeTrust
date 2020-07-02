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
import developer.carlos.silva.adapters.GenreAdapter
import developer.carlos.silva.interfaces.AnimeLoaderGenreListener
import developer.carlos.silva.models.Genre
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.episodes_fragment.*
import kotlinx.android.synthetic.main.genre_fragment.*
import kotlinx.android.synthetic.main.genre_fragment.recyclerview

class GenreFragment : Fragment() {

    private val mAdapter = GenreAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.genre_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadGenres()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mAdapter.mGenreFragment = this

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager =
            GridLayoutManager(context!!, 2, GridLayoutManager.VERTICAL, false)


        (activity as MainActivity).let {
            recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0 && it.bottomview.isShowing()) {
                        it.bottomview.hide()
                    }else if(dy <= 0 && !it.bottomview.isShowing()) {
                        it.bottomview.show()
                    }
                }
            })
        }

        recyclerview.adapter = mAdapter
    }

    private fun loadGenres() {
        LoaderAnimes.loadGenres(object : AnimeLoaderGenreListener {
            override fun onLoad(genres: MutableList<Genre>) {
                mAdapter.addItems(genres)
            }
        })
    }

    companion object {
        fun newInstance() = GenreFragment()
    }
}
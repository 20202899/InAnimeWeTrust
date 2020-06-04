package developer.carlos.silva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.adapters.SearchAdapter
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.calendar_fragment.*
import kotlinx.android.synthetic.main.search_fragment.*
import kotlinx.android.synthetic.main.search_fragment.recyclerview


class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    val mAdapter = SearchAdapter()
    private var mMainActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        mMainActivity?.hideKeyboard()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMainActivity = activity as MainActivity
        mMainActivity?.search_view?.setOnQueryTextListener(this)
        mAdapter.mMainActivity = mMainActivity!!
        val closeButton = mMainActivity?.search_view?.findViewById(R.id.search_close_btn) as ImageView?
        closeButton?.setOnClickListener {
            mMainActivity?.hideKeyboard()
            val et =  mMainActivity?.search_view?.findViewById(R.id.search_src_text) as EditText?
            et?.setText("")

            mAdapter.clean()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity
        val gridLayoutManager = GridLayoutManager(
            context, 2,
            GridLayoutManager.VERTICAL, false
        )

        swiperefresh.isEnabled = false

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

        recyclerview.setHasFixedSize(true)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup () {
            override fun getSpanSize(position: Int): Int {
                return 1
            }

        }

        recyclerview.layoutManager = gridLayoutManager
        recyclerview.adapter = mAdapter
    }

    companion object {
        fun newInstance() = SearchFragment()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {

        swiperefresh.isEnabled = true
        swiperefresh.isRefreshing = true

        LoaderAnimes.search(object : AnimeLoaderListener {
            override fun onLoad(animes: MutableList<Any>) {
                swiperefresh.isRefreshing = false
                swiperefresh.isEnabled = false
                mAdapter.addItems(animes)
            }

        }, query)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

}
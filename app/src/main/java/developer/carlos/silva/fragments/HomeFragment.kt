package developer.carlos.silva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.adapters.HomeRecylerviewAdapter
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_frament.*

class HomeFragment : Fragment(), AnimeLoaderListener {

    private val mAdapter = HomeRecylerviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_frament, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        swiperefresh.isEnabled = true
        swiperefresh.isRefreshing = true

        swiperefresh.setOnRefreshListener {
            mAdapter.cleanItems()
            LoaderAnimes.loadAnimes(this)
        }

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

        mAdapter.mHomeFragment = this

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,
            false)
        recyclerview.adapter = mAdapter

        LoaderAnimes.loadAnimes(this)
    }

    override fun onResume() {
        super.onResume()
        mAdapter.animatePause = true
    }

    override fun onLoad(animes: MutableList<Any>) {
        mAdapter.addItems(animes)

        swiperefresh.isRefreshing = false
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
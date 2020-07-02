package developer.carlos.silva.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.adapters.CalendarAdapter
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.network.LoaderAnimes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.calendar_fragment.recyclerview

class CalendarFragment : Fragment() {

    val mAdapter = CalendarAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.calendar_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        mAdapter.mActivity = mainActivity
        val dividerItemDecoration = DividerItemDecoration(
            context,
            DividerItemDecoration.VERTICAL
        )

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        recyclerview.adapter = mAdapter

        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.toolbar_layout.title = "Calendário"
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

        LoaderAnimes.loadCalendar(object : AnimeLoaderListener{
            override fun onLoad(animes: MutableList<Any>) {
                mAdapter.addItems(animes)
            }

        })
    }

    companion object {
        const val FRAGMENT_ID = "Calendar"
        fun newInstance() = CalendarFragment()
    }
}
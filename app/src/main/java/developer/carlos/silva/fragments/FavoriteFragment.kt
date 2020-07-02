package developer.carlos.silva.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import developer.carlos.silva.R
import developer.carlos.silva.activities.FavoriteActivity
import developer.carlos.silva.adapters.FavoriteAdapter
import developer.carlos.silva.database.DatabaseServices
import developer.carlos.silva.singletons.MainController
import kotlinx.android.synthetic.main.favorite_fragment.recyclerview

class FavoriteFragment : Fragment() {

    private lateinit var mSharedPreferences: SharedPreferences
    private val mAdapter = FavoriteAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        loadAnimes()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteActivity = activity as FavoriteActivity
        mAdapter.mActivity = favoriteActivity
//        val dividerItemDecoration = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        mSharedPreferences = activity!!.getSharedPreferences("Sets", Context.MODE_PRIVATE)

//        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                if (dy > 0 && mainActivity.bottomview.isShowing()) {
//                    mainActivity.bottomview.hide()
//                }else if(dy <= 0 && !mainActivity.bottomview.isShowing()) {
//                    mainActivity.bottomview.show()
//                }
//            }
//        })

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.VERTICAL,
            false
        )

//        val drawable = ContextCompat.getDrawable(context!!, R.drawable.divider_item_list)
//        dividerItemDecoration.setDrawable(drawable!!)
//        recyclerview.addItemDecoration(dividerItemDecoration)

        recyclerview.adapter = mAdapter
    }

    private fun loadAnimes () {
        Thread{
            val db = DatabaseServices.getDataBaseInstance(context!!)
            val daoAnime = db.dataAnimeDao()
            val animes = daoAnime.getAll()
            MainController.getInstance()?.getHandler()?.post {
                mAdapter.addItems(animes)
            }
        }.start()
    }

    companion object {
        fun newInstance() = FavoriteFragment()
    }

}
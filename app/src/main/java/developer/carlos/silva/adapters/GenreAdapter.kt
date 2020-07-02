package developer.carlos.silva.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.fragments.AnimesFragment
import developer.carlos.silva.fragments.CalendarFragment
import developer.carlos.silva.fragments.GenreFragment
import developer.carlos.silva.models.Genre
import kotlinx.android.synthetic.main.activity_main.*

class GenreAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    lateinit var mGenreFragment: GenreFragment

    private val items = mutableListOf<Genre>()

    fun addItems(items: MutableList<Genre>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.genre_item_list, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val item = items[position]
            holder.text1.text = item.title

//            (mHomeFragment?.activity as MainActivity?)
//                ?.app_bar?.setExpanded(true, true)

            holder.itemView.setOnClickListener {
                (mGenreFragment.activity as MainActivity).apply {
                    app_bar.setExpanded(true, true)
                }

                val transaction = mGenreFragment.childFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                transaction
                    .add(R.id.container_animes, AnimesFragment.newInstance(item.title, item.link), AnimesFragment.FRAGMENT_ID)
                    .addToBackStack(null)
                    .commit()
            }

        }
    }

    inner class ItemViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

}
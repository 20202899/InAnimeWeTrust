package developer.carlos.silva.adapters

import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.models.Anime

class CalendarAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<Any>()
    lateinit var mActivity: MainActivity
    fun addItems(items: MutableList<Any>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) {
            return 0
        }else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.calendar_item_list_view, parent,
                    false
                )
            )
        }else {
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.calendar_item_list_view, parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val anime = items[position] as Anime
            holder.text1.text = anime.title
            holder.text1.gravity = Gravity.START
            holder.text1.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))

            holder.itemView.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(
                    context, AnimeActivity::class.java
                )
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity).toBundle()
                intent.putExtra("data", anime)
                context.startActivity(intent, bundle)
            }
        }

        if (holder is HeaderViewHolder) {
            val title = items[position] as String
            holder.text1.text = title
            holder.text1.gravity = Gravity.CENTER
            holder.text1.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.colorAccent))
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

}
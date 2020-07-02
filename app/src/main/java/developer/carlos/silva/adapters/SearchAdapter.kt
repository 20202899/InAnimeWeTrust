package developer.carlos.silva.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.models.Anime

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()
    lateinit var mMainActivity: MainActivity
    fun addItems(items: MutableList<Any>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun addAll(items: MutableList<Any>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.anime_search_item_view,
                parent, false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val anime = items[position] as Anime

            holder.text1.text = anime.title

            holder.itemView.setOnClickListener {
                val intent = Intent(
                    holder.itemView.context, AnimeActivity::class.java
                )
                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(mMainActivity)
                    .toBundle()

                intent.putExtra("data", anime)
                holder.itemView.context.startActivity(intent, bundle)
            }

            Glide.with(holder.itemView.context)
                .load(anime.imagePath)
                .into(holder.img)
        }
    }

    fun clean() {
        items.clear()
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.findViewById<ImageView>(R.id.img)
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

}
package developer.carlos.silva.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.database.models.AnimeAndEpisodes
import developer.carlos.silva.models.Anime
import developer.carlos.silva.database.models.DataAnime

class FavoriteAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = mutableListOf<AnimeAndEpisodes>()

    fun addItems(items: MutableList<AnimeAndEpisodes>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.favorite_item_list_view, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val dataAnime = items[position].dataAnime
            holder.text1.text = dataAnime.title
            holder.text2.text = dataAnime.sinopse

            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(dataAnime.capa)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.DEBUG
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        holder.image.setImageBitmap(resource)
                    }

                })

            holder.itemView.setOnClickListener {
                val anime = Anime()
                anime.imagePath = dataAnime.capa
                anime.title = dataAnime.title
                anime.link = dataAnime.link
                val context = holder.itemView.context
                val intent = Intent(
                    context, AnimeActivity::class.java
                )

                intent.putExtra("data", anime)
                context.startActivity(intent)
            }
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
        var text2 = itemView.findViewById<TextView>(android.R.id.text2)
        var image = itemView.findViewById<ImageView>(R.id.img)
    }

}
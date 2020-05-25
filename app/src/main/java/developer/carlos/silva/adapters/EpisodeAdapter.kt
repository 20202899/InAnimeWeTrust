package developer.carlos.silva.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.reflect.TypeToken
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.dialogs.LoadDialog
import developer.carlos.silva.models.Anime
import developer.carlos.silva.models.Player
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.activity_anime.*
import org.jsoup.Jsoup

class EpisodeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var RGB_COLOR_DOMINANT = -1
    var RGB_COLOR_TEXT = -1

    lateinit var mActivity: AnimeActivity

    var IMG_PATH = ""

    private val items = mutableListOf<Any>()

    fun addItems(items: MutableList<Any>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.new_episode_item_view, parent, false)
            )
        } else {
            HeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.new_episode_header_view, parent, false)
            )
        }
    }

    override fun getItemCount(): Int = items.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            var anime = items[position - 1] as Anime

            if (mActivity.getIds().contains(anime.videoId)) {
                holder.cardview.setCardBackgroundColor(RGB_COLOR_TEXT)
            } else {
                holder.cardview.setCardBackgroundColor(RGB_COLOR_DOMINANT)
            }

            holder.text1.text = anime.title

            holder.itemView.setOnClickListener {

                mActivity.saveSets(anime.videoId.toString())

                holder.cardview.setCardBackgroundColor(RGB_COLOR_TEXT)

                LoadDialog.show(mActivity.supportFragmentManager)

                Thread {
                    val doc = Jsoup.connect(anime.link).get()
                    val scripts = doc.select("script")
                    val script = scripts.find { it.toString().contains("sources:") }.toString()
                    val type = object : TypeToken<MutableList<Player>>() {}.type
                    val result = Utils.uncodedScriptText<MutableList<Player>>(
                        script,
                        type
                    )

                    MainController.getInstance()?.getHandler()?.post {
                        val dialog = AlertDialog.Builder(mActivity)
                            .setTitle(anime.title)
                            .setItems(result.map { it.label }
                                .toTypedArray()) { dialogInterface, i ->
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setDataAndType(
                                    Uri.parse(result[i].file),
                                    result[i].type
                                )
                                mActivity.startActivity(intent)
                            }.setOnDismissListener {
                                LoadDialog.hide(mActivity.supportFragmentManager)
                            }.create()

                        dialog.setOnShowListener {
                            LoadDialog.hide(mActivity.supportFragmentManager)
                        }

                        dialog.show()
                    }

                    Log.d("%s", anime.link)
                }.start()
            }

        } else {
            val newHolder = holder as HeaderViewHolder
            Glide.with(mActivity)
                .asBitmap()
                .load(IMG_PATH)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        mActivity.createPalette(resource)
                        newHolder.image.setImageBitmap(resource)
                    }

                })
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardview = itemView.findViewById<CardView>(R.id.cardview)
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.img)
    }

}
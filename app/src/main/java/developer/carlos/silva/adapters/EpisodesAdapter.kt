package developer.carlos.silva.adapters

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.reflect.TypeToken
import developer.carlos.silva.R
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.dialogs.LoadDialog
import developer.carlos.silva.fragments.EpisodesFragment
import developer.carlos.silva.models.Anime
import developer.carlos.silva.models.Player
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.episodes_fragment.*
import org.jsoup.Jsoup

class EpisodesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lastIndexAnimation: Int = -1
    private var items = mutableListOf<Any>()
    lateinit var mEpisodesFragment: EpisodesFragment

    fun addItems(items: MutableList<Any>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.new_episode_item_list_view, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            var anime = items[position] as Anime
            holder.text1.text = anime.title

            holder.itemView.setOnClickListener {

                (mEpisodesFragment.activity as MainActivity).saveSets(anime.videoId.toString())

                LoadDialog.show(mEpisodesFragment.fragmentManager!!)

                Thread {
                    try {
                        val doc = Jsoup.connect(anime.link).get()
                        val scripts = doc.select("script")
                        val script = scripts.find { it.toString().contains("sources:") }.toString()
                        val type = object : TypeToken<MutableList<Player>>() {}.type
                        val result = Utils.uncodedScriptText<MutableList<Player>>(
                            script,
                            type
                        )

                        MainController.getInstance()?.getHandler()?.post {
                            val dialog = AlertDialog.Builder(mEpisodesFragment.context!!)
                                .setTitle(anime.title)
                                .setItems(result.map { it.label }
                                    .toTypedArray()) { _, i ->
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.setDataAndType(
                                        Uri.parse(result[i].file),
                                        result[i].type
                                    )
                                    mEpisodesFragment.context!!.startActivity(intent)
                                }.setOnDismissListener {

                                }.create()

                            dialog.setOnShowListener {
                                LoadDialog.hide(mEpisodesFragment.fragmentManager!!)
                            }

                            dialog.show()
                        }

                        Log.d("%s", anime.link)
                    }catch (e: Exception) {

                    }
                }.start()
            }

            Glide.with(holder.itemView.context)
                .asBitmap()
                .load(anime.imagePath)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.DEBUG
                    }

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        holder.image.setImageBitmap(resource)
                        if (position > lastIndexAnimation) {
                            val objectAnimator = ObjectAnimator.ofFloat(holder.itemView,
                                "translationX", -1500f, 0f)

                            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
                            objectAnimator.duration = 600
                            objectAnimator.start()
                            holder.itemView.visibility = CardView.VISIBLE
                            lastIndexAnimation = position
                        }
                    }

                })
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.img)
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

}
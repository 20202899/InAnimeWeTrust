package developer.carlos.silva.adapters

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.database.models.DataEpisode
import developer.carlos.silva.dialogs.LoadDialog
import developer.carlos.silva.models.Player
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.content_anime.*
import org.jsoup.Jsoup
import java.lang.Exception

class EpisodeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var RGB_COLOR_DOMINANT = -1
    var RGB_COLOR_TEXT = -1
    var mDataAnime: DataAnime? = null
    lateinit var mActivity: AnimeActivity
    private var mHeaderViewHolder: HeaderViewHolder? = null
    var IMG_PATH = ""

    private var isContinue = false

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
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.new_episode_item_view, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long {
        return (items[position] as DataEpisode).id.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val anime = items[position] as DataEpisode
            holder.setIsRecyclable(false)
            if (!isContinue) {
                if (isContinue()) {
                    mActivity.start_watch?.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.start_watch_selected)
                    mActivity.start_watch?.text = "Continuar"

                    mActivity.start_watch?.startAnimation(
                        AnimationUtils
                            .loadAnimation(mActivity, R.anim.scale_fade_in)
                    )

                    mActivity.start_watch?.visibility = Button.VISIBLE

                    isContinue = true
                }
            }

            if (mActivity.getIds().contains(anime.id)) {
                holder.itemView.background = ContextCompat.getDrawable(mActivity, R.drawable.start_watch_selected)
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(
                    mActivity,
                    R.color.colorPrimary
                ))
            }

//            if (position > lastIndexAnimation) {
//                val objectAnimator = ObjectAnimator.ofFloat(
//                    holder.itemView,
//                    "translationX", -1500f, 0f
//                )
//
//                objectAnimator.interpolator = AccelerateDecelerateInterpolator()
//                objectAnimator.duration = 600
//                objectAnimator.start()
//                holder.itemView.visibility = CardView.VISIBLE
//                lastIndexAnimation = position
//            }

            holder.text1.text = anime.title

            holder.itemView.setOnClickListener {

                if (!isContinue && mActivity.start_watch.visibility == Button.GONE) {
                    mActivity.start_watch.background =
                        ContextCompat.getDrawable(mActivity, R.drawable.start_watch_selected)
                    mActivity.start_watch.text = "Continuar"

                    mActivity.start_watch.startAnimation(
                        AnimationUtils
                            .loadAnimation(mActivity, R.anim.scale_fade_in)
                    )

                    mActivity.start_watch.visibility = Button.VISIBLE
                }

                mActivity.saveSets(anime.id.toString())

                holder.itemView.background = ContextCompat.getDrawable(mActivity, R.drawable.start_watch_selected)

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
                        val dialog = AlertDialog.Builder(mActivity, R.style.AppTheme_Dialog)
                            .setTitle(anime.title)
                            .setNeutralButton(android.R.string.ok) { dialogInterface, i ->
                            }
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
            newHolder.setIsRecyclable(true)
            newHolder.text2.text = mDataAnime?.sinopse

            if (IMG_PATH.isNotEmpty()) {
                Picasso.get()
                    .load(IMG_PATH)
                    .fit()
                    .noFade()
                    .into(holder.image, object : Callback {
                        override fun onSuccess() {
                            mActivity.finishTransition()
                        }

                        override fun onError(e: Exception?) {
                            mActivity.finishTransition()
                        }

                    })
            }


//            Glide.with(mActivity)
//                .asBitmap()
//                .load(IMG_PATH)
//                .apply (RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        Log.DEBUG
//                    }
//
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
//                    ) {
//                        newHolder.image.setImageBitmap(resource)
////                        if (holder.image.visibility == CardView.GONE) {
////                            holder.image.visibility = CardView.VISIBLE
////                        }
//
//                        mActivity.supportStartPostponedEnterTransition()
//
//                    }
//
//                })
        }
    }

    fun startOrContinue() {
        Thread {
            val ids = mActivity.getIds()
            val saveIds = mutableListOf<DataEpisode>()
            items.forEach {
                val episode = it as DataEpisode
                if (ids.contains(episode.id))
                    saveIds.add(it)
            }

            val index = items.indexOf(saveIds.last())
            MainController.getInstance()?.getHandler()?.post {
                mActivity.recyclerview.scrollToPosition(index)
            }
        }.start()
    }

    fun isContinue (): Boolean {
        val ids = mActivity.getIds()
        val saveIds = mutableListOf<DataEpisode>()
        items.forEach {
            val episode = it as DataEpisode
            if (ids.contains(episode.id))
                saveIds.add(it)
        }

        return saveIds.size > 0
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardview = itemView.findViewById<LinearLayout>(R.id.cardview)
        var text1 = itemView.findViewById<TextView>(android.R.id.text1)
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.findViewById<ImageView>(R.id.img)
        var text2 = itemView.findViewById<TextView>(android.R.id.text2)
        var start_watch = itemView.findViewById<Button>(R.id.start_watch)
    }

}
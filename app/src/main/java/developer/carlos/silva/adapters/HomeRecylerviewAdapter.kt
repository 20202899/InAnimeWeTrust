package developer.carlos.silva.adapters

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeScroll
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import developer.carlos.silva.activities.MainActivity
import developer.carlos.silva.dialogs.LoadDialog
import developer.carlos.silva.fragments.CalendarFragment
import developer.carlos.silva.fragments.EpisodesFragment
import developer.carlos.silva.fragments.HomeFragment
import developer.carlos.silva.fragments.PlayFragment
import developer.carlos.silva.models.Anime
import developer.carlos.silva.models.Player
import developer.carlos.silva.singletons.MainController
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import java.util.*


class HomeRecylerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()
    var mHomeFragment: HomeFragment? = null
    private val mHandler = Handler()
    private val mTimer = Timer()
    var animatePause = false

    fun addItems(items: MutableList<Any>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun cleanItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.title_header,
                    parent, false
                )
            )
        } else {
            ItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.home_item_view,
                    parent, false
                )
            )
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is String) {
            0
        } else {
            1
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        itemView.setOnTouchListener(OnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        })

        if (holder.itemView.visibility == RecyclerView.GONE) {
            val objectAnimator = ObjectAnimator.ofFloat(holder.itemView, "x", 1500f, 0f)
            objectAnimator.duration = 600
            objectAnimator.interpolator = AccelerateDecelerateInterpolator()

            objectAnimator.start()

            holder.itemView.visibility = RecyclerView.VISIBLE
        }

        if (itemView is RecyclerView) {

            val itemViewHolder = (holder as ItemViewHolder)
            val adapter = itemViewHolder.mItemRecylerviewAdapter
            val linearLayoutManager = object : LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL, false
            ) {
                override fun canScrollHorizontally(): Boolean {
                    return position != 5
                }
            }

            adapter.mRecyclerView = itemView
            adapter.column = position

            if (position == 5 && !adapter.animNextPrev) {
                adapter.animNextPrev = true
                mTimer.schedule(object : TimerTask() {
                    override fun run() {
                        try {

                            if (adapter.inAnimation && !animatePause)
                                return

                            val max = (items[position] as MutableList<*>).size

                            mHandler.post {
                                itemView.scrollToPosition(
                                    if (!adapter.endList) {
                                        if (++adapter.count_column5 <= (max - 1)) {
                                            adapter.count_column5
                                        } else {
                                            adapter.endList = !adapter.endList
                                            adapter.count_column5 = max - 1
                                            adapter.count_column5
                                        }
                                    } else {
                                        if (--adapter.count_column5 >= 0) {
                                            adapter.count_column5
                                        } else {
                                            adapter.endList = !adapter.endList
                                            adapter.count_column5 = 0
                                            adapter.count_column5
                                        }
                                    }
                                )
                            }

                            if (!adapter.endList) {
                                adapter.slideNext()
                            } else {
                                adapter.slidePrev()
                            }

                        } catch (e: IndexOutOfBoundsException) {

                        }
                    }

                }, 12000, 12000)
            }

            itemView.setHasFixedSize(true)
            itemView.layoutManager = linearLayoutManager
            itemView.adapter = adapter

            adapter.notifyDataSetChanged()
        } else {

            val header = holder as HeaderViewHolder
            val item = items[position] as String

            if (position == 4) {
                holder.textView2.setOnClickListener {
                                       (mHomeFragment?.activity as MainActivity?)?.apply {
                        app_bar.setExpanded(true, true)
                        val childFM = mFragmentAdapter.fragments[0].childFragmentManager
                        val transaction = childFM.beginTransaction()
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        transaction
                            .add(R.id.container_main, EpisodesFragment.newInstance(), EpisodesFragment.FRAGMENT_ID)
                            .addToBackStack(null)
                            .commit()
                    }

                }
            }

            if (position == 6) {
                holder.textView2.setOnClickListener {
                    (mHomeFragment?.activity as MainActivity?)?.apply {
                        app_bar.setExpanded(true, true)
                        val childFM = mFragmentAdapter.fragments[0].childFragmentManager
                        val transaction = childFM.beginTransaction()
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        transaction
                            .add(R.id.container_main, CalendarFragment.newInstance(), CalendarFragment.FRAGMENT_ID)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

            if (item == "Tendência") {
                header.textView1.text = item
                header.textView2.visibility = TextView.INVISIBLE
            } else if (item == "Lançamentos do Dia") {
                header.textView1.text = item
                holder.textView2.text = "Ver Calendário"
                header.textView2.visibility = TextView.VISIBLE
            } else if (item == "Recentes") {
                header.textView1.text = item
                header.textView2.visibility = TextView.INVISIBLE
            } else {
                header.textView1.text = item
                header.textView2.visibility = TextView.VISIBLE
            }
        }


    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mItemRecylerviewAdapter = ItemRecylerviewAdapter()
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView1 = itemView.findViewById<TextView>(android.R.id.text1)
        var textView2 = itemView.findViewById<TextView>(android.R.id.text2)
    }

    inner class ItemRecylerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var mRecyclerView: RecyclerView? = null

        var column = 0

        var count_column5 = 0

        val mGson = Gson()

        var animNextPrev = false

        var endList = false

        private var lastIndex = -1

        var inAnimation = false

        fun slideNext() {
            if (inAnimation) {
                return
            }

            val bounds = ChangeScroll()
            inAnimation = true
            bounds.interpolator = AccelerateDecelerateInterpolator()

            bounds.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    inAnimation = false
                }

                override fun onTransitionResume(transition: Transition) {

                }

                override fun onTransitionPause(transition: Transition) {

                }

                override fun onTransitionCancel(transition: Transition) {

                }

                override fun onTransitionStart(transition: Transition) {

                }
            })

            TransitionManager.beginDelayedTransition(mRecyclerView!!, bounds)
        }

        fun slidePrev() {

            if (inAnimation) {
                return
            }

            val bounds = ChangeScroll()
            inAnimation = true
            bounds.interpolator = AccelerateDecelerateInterpolator()

            bounds.addListener(object : Transition.TransitionListener {
                override fun onTransitionEnd(transition: Transition) {
                    inAnimation = false
                }

                override fun onTransitionResume(transition: Transition) {

                }

                override fun onTransitionPause(transition: Transition) {

                }

                override fun onTransitionCancel(transition: Transition) {

                }

                override fun onTransitionStart(transition: Transition) {

                }
            })

            TransitionManager.beginDelayedTransition(mRecyclerView!!, bounds)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 3) {
                ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.new_anime_item_view, parent, false)
                )
            } else if (viewType == 5) {
                ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.episode_item_view, parent, false)
                )
            } else {
                ItemViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.anime_item_view, parent, false)
                )
            }
        }

        override fun getItemViewType(position: Int): Int {
            return column
        }

        override fun getItemCount(): Int {
            return (items[column] as MutableList<*>).size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is ItemViewHolder) {
                val anime = (items[column] as MutableList<Anime>)[position]

                if (column == 5) {

                    val max = (items[column] as MutableList<*>).size

                    holder.itemView.setOnClickListener {

                        (mHomeFragment?.activity as MainActivity)?.saveSets(anime.videoId.toString())

                        LoadDialog.show(mHomeFragment?.fragmentManager!!)

                        Thread {
                            try {
                                val doc = Jsoup.connect(anime.link).get()
                                val scripts = doc.select("script")
                                val script =
                                    scripts.find { it.toString().contains("sources:") }.toString()
                                val type = object : TypeToken<MutableList<Player>>() {}.type
                                val result = Utils.uncodedScriptText<MutableList<Player>>(
                                    script,
                                    type
                                )

                                MainController.getInstance()?.getHandler()?.post {
                                    val dialog = AlertDialog.Builder(
                                        mRecyclerView!!.context,
                                        R.style.AppTheme_Dialog
                                    )
                                        .setTitle(anime.title)
                                        .setNeutralButton(android.R.string.ok) { dialogInterface, i ->
                                            dialogInterface.dismiss()
                                        }
                                        .setItems(result.map { it.label }
                                            .toTypedArray()) { dialogInterface, i ->

                                            AlertDialog.Builder(
                                                mRecyclerView!!.context,
                                                R.style.AppTheme_Dialog
                                            ).setTitle("Opções de Player")
                                                .setItems(
                                                    arrayOf(
                                                        "Principal",
                                                        "Externo",
                                                        "Download - TRABALHANDO"
                                                    )
                                                ) { dialogInterface, j ->
                                                    if (j == 0) {
                                                        val fragmentManager =
                                                            mHomeFragment?.fragmentManager
                                                        fragmentManager
                                                            ?.beginTransaction()
                                                            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                                            ?.replace(
                                                                android.R.id.content,
                                                                PlayFragment.newInstance(
                                                                    result[i].file,
                                                                    result[i].type
                                                                )
                                                            )
                                                            ?.addToBackStack(null)
                                                            ?.commit()
                                                    } else if(j == 1) {
                                                        val intent =
                                                            Intent(Intent.ACTION_VIEW)
                                                        intent.setDataAndType(
                                                            Uri.parse(
                                                                result[i].file
                                                            ),   result[i].type
                                                        )
                                                        mHomeFragment?.context?.startActivity(intent)
                                                    }
                                                }.show()

                                        }.setOnDismissListener {
                                            LoadDialog.hide(mHomeFragment?.fragmentManager!!)
                                        }.create()

                                    dialog.setOnShowListener {
                                        LoadDialog.hide(mHomeFragment?.fragmentManager!!)
                                    }

                                    dialog.show()
                                }

                                Log.d("%s", anime.link)
                            } catch (e: Exception) {

                            }
                        }.start()
                    }

                    Glide.with(holder.itemView.context)
                        .load(anime.imagePath)
                        .apply(
                            RequestOptions()
                                .override(SIZE_ORIGINAL)
                                .format(DecodeFormat.PREFER_ARGB_8888)
                        )
                        .into(holder.img)

                    holder.next.setOnClickListener {

                        if (inAnimation) {
                            return@setOnClickListener
                        }

                        endList = false

                        mRecyclerView?.scrollToPosition(
                            if (++count_column5 <= (max - 1)) {
                                count_column5
                            } else {
                                count_column5 = max - 1
                                count_column5
                            }
                        )

                        slideNext()

                    }
                    holder.prev.setOnClickListener {

                        if (inAnimation) {
                            return@setOnClickListener
                        }

                        endList = true

                        mRecyclerView?.scrollToPosition(
                            if (--count_column5 >= 0) {
                                count_column5
                            } else {
                                count_column5 = 0
                                count_column5
                            }
                        )

                        slidePrev()
                    }

                } else {

                    holder.itemView.setOnClickListener(null)
                    val listener = View.OnClickListener {
                        val activity = mHomeFragment?.activity
                        val intent = Intent(
                            mRecyclerView!!.context, AnimeActivity::class.java
                        )

                        intent.putExtra("data", anime)

                        val bundle = if (column == 3) {
                            holder.img.transitionName = null
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                activity!!
                            ).toBundle()
                        } else {
                            holder.img.transitionName =
                                activity?.getString(R.string.transition_name)
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                activity!!,
                                Pair(holder.itemView, activity.getString(R.string.transition_name))
                            ).toBundle()
                        }

                        animatePause = true

                        mRecyclerView?.context?.startActivity(intent, bundle)
                    }

                    holder.itemView.setOnClickListener(listener)

                    Glide.with(holder.itemView.context)
                        .asBitmap()
                        .apply(RequestOptions().override(450, 550))
                        .load(anime.imagePath)
                        .into(holder.img)
                }

                if (column != 3) {
                    holder.text1.text = anime.title
                }
            }
        }

        inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var img = itemView.findViewById<ImageView>(R.id.img)
            var text1 = itemView.findViewById<TextView>(android.R.id.text1)
            var next = itemView.findViewById<ImageView>(R.id.next)
            var prev = itemView.findViewById<ImageView>(R.id.prev)
        }

    }

}
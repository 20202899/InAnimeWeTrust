package developer.carlos.silva.fragments

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import developer.carlos.silva.R
import developer.carlos.silva.extensions.hideSystemUI
import developer.carlos.silva.extensions.showSystemUI
import developer.carlos.silva.utils.Utils
import kotlinx.android.synthetic.main.episode_item_view.*
import kotlinx.android.synthetic.main.play_fragment.*


class PlayFragment : Fragment() {

    private var player: SimpleExoPlayer? = null
    private var clickMills = 0.toLong()
    private var position = 0.toLong()
    var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clickMills = System.currentTimeMillis()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return if (savedInstanceState != null) {
                isFullScreen = savedInstanceState.getBoolean("fullScreen")
            if (isFullScreen) {
                inflater.inflate(R.layout.play_fullscreen_fragment, container, false)
            }else {
                inflater.inflate(R.layout.play_fragment, container, false)
            }

        }else {
            inflater.inflate(R.layout.play_fragment, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getView()?.isFocusableInTouchMode = true
        getView()?.requestFocus()
        getView()?.setOnKeyListener { v, keyCode, event ->


            if (keyCode == KeyEvent.KEYCODE_BACK) {
                val fragment = activity?.supportFragmentManager?.findFragmentById(android.R.id.content)
                if (fragment != null && !isFullScreen) {
                    false
                }
                else if (fragment != null && isFullScreen) {
                    videoViewContainer.useController = false
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    isFullScreen = false
                    true
                }
                else {
                    false
                }
            }else {
                false
            }

        }

        videoViewContainer.useController = isFullScreen

        initFullScreenButton()

        videoViewContainer.setOnTouchListener { p0, p1 ->

            if ((System.currentTimeMillis() - clickMills) > CLICK_TIME && !isFullScreen) {
                showController()
            }

            return@setOnTouchListener false
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (savedInstanceState != null) {
            position = savedInstanceState.getLong("position", 0)
        }
    }

    private fun initPlay() {
        val dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory("Android")

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(arguments?.getString("filePath")))

        player = ExoPlayerFactory.newSimpleInstance(context!!)
        videoViewContainer.player = player

        player?.prepare(mediaSource)

        player?.seekTo(position)

        player?.playWhenReady = true
    }

    private fun initFullScreenButton() {

        val field = PlayerView::class.java.getDeclaredField("controller")
        field.isAccessible = true
        val controller = field.get(videoViewContainer) as PlayerControlView
        controller.addView(ImageView(context!!).apply {
            id = android.R.id.edit
            layoutParams = FrameLayout.LayoutParams(Utils.dpToPx(24f, resources).toInt(), Utils.dpToPx(24f, resources).toInt()).apply {
                gravity = Gravity.END or Gravity.BOTTOM
                setMargins(0, 0, 6, Utils.dpToPx(43f, resources).toInt())
            }

            if (isFullScreen) {
                setImageResource(R.drawable.exo_controls_fullscreen_exit)
            }else {
                setImageResource(R.drawable.exo_controls_fullscreen_enter)
            }

            setOnClickListener {
                if (!isFullScreen) {
                    videoViewContainer.useController = true
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                    isFullScreen = true
                }else {
                    videoViewContainer.useController = false
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    isFullScreen = false
                }
            }
        })
    }

    private fun showController () {

        if (!videoViewContainer.useController) {
            val field = PlayerView::class.java.getDeclaredField("controller")
            field.isAccessible = true
            val controller = field.get(videoViewContainer) as PlayerControlView
            if (!controller.isShown) {

                if (controller.player == null) {
                    controller.showTimeoutMs = 2000
                }

                controller.player = player

                controller.show()
            }else {
                controller.hide()
            }
        }
    }

    private fun hideController () {
        if (!videoViewContainer.useController) {
            val field = PlayerView::class.java.getDeclaredField("controller")
            field.isAccessible = true
            val controller = field.get(videoViewContainer) as PlayerControlView

            if (controller.player == null)
                controller.player = player

            controller.hide()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("position", if (player == null) {
            position
        }else {
            player!!.currentPosition
        })
        outState.putBoolean("fullScreen", isFullScreen)
    }

    override fun onStop() {
        super.onStop()
        if (player != null) {
            position = player?.currentPosition ?: 0
            player?.stop()
            player?.release()
            player = null
            videoViewContainer.player = null
        }
    }

    override fun onResume() {
        super.onResume()
        initPlay()
    }

    companion object {

        const val CLICK_TIME: Long = 1500

        fun newInstance(filePath: String, fileType: String): PlayFragment {
            val fragment = PlayFragment()
            val bundle = Bundle()
            bundle.putString("filePath", filePath)
            bundle.putString("fileType", fileType)
            fragment.arguments = bundle
            return fragment
        }
    }

}
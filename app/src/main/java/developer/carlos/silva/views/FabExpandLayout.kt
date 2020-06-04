package developer.carlos.silva.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.transition.ChangeBounds
import androidx.transition.Transition
import androidx.transition.TransitionManager
import developer.carlos.silva.R
import developer.carlos.silva.activities.AnimeActivity
import kotlinx.android.synthetic.main.activity_anime.*
import kotlinx.android.synthetic.main.fab_expand_layout.view.*
import kotlinx.android.synthetic.main.fab_expand_layout.view.img

class FabExpandLayout : FrameLayout, ViewTreeObserver.OnGlobalLayoutListener {

    private var isExpanded = false

    private val options = arrayOf("Assistindo", "Concluído")

    lateinit var mActivity: AnimeActivity

    private lateinit var defaultParams: ViewGroup.LayoutParams

    private var inAnimation = false

    var isAdded = false

    private var addOnClickListener: OnClickListener? = null
    private var cancelOnClickListener: OnClickListener? = null

    var title = ""
    var isWatching = false

    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs) {

        initView()

        Log.DEBUG
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.fab_expand_layout, this)
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        Log.DEBUG
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        Log.DEBUG
    }

    fun onClickListener(onClickListener: OnClickListener?) {
        setOnClickListener {

            showDialogExpand()

            onClickListener?.onClick(it)
        }
    }

    private fun hideDialogExpand() {

        if (inAnimation) {
            return
        }

        mActivity.main_container.removeView(mActivity.main_container.findViewById(android.R.id.edit))

        inAnimation = true

        val changeBounds = ChangeBounds()
        val params = this.layoutParams as CoordinatorLayout.LayoutParams
        changeBounds.interpolator = AccelerateDecelerateInterpolator()
        changeBounds.duration = 700
        params.width = defaultParams.width
        params.height = defaultParams.height

        params.gravity = Gravity.BOTTOM or Gravity.END

        this.layoutParams = params
        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                background = ContextCompat.getDrawable(context, android.R.color.transparent)
                inAnimation = false
                isExpanded = false
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

        val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)

        fadeIn.interpolator = AccelerateDecelerateInterpolator()
        fadeIn.duration = 1000
        fadeOut.interpolator = AccelerateDecelerateInterpolator()
        fadeOut.duration = 500

        img.startAnimation(fadeIn)
        info_menu.startAnimation(fadeOut)

        img.background =
            ContextCompat.getDrawable(context, R.drawable.fab_expand_background)

        img.visibility = ImageView.VISIBLE
        info_menu.visibility = GONE
        TransitionManager.beginDelayedTransition(this, changeBounds)

    }

    private fun showDialogExpand() {

        if (inAnimation) {
            return
        }

        mActivity.main_container.addView(View(context).apply {
            background = context.getDrawable(android.R.color.black)
            alpha = 0.7f
            id = android.R.id.edit
            isEnabled = true
            requestFocus()
            isClickable = true
        }, CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT))

        inAnimation = true

        val changeBounds = ChangeBounds()
        changeBounds.interpolator = OvershootInterpolator()
        changeBounds.duration = 600

        add.text = if (isAdded) {
            "REMOVER"
        }else {
            "ADD"
        }

        changeBounds.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {
                val params = this@FabExpandLayout.layoutParams as CoordinatorLayout.LayoutParams
                val newChangeBounds = ChangeBounds()
                params.width = CoordinatorLayout.LayoutParams.MATCH_PARENT
                params.height = 600
                this@FabExpandLayout.layoutParams = params
                background = ContextCompat.getDrawable(context, R.drawable.fab_unexpand_background)

                newChangeBounds.addListener(object : Transition.TransitionListener {
                    override fun onTransitionEnd(transition: Transition) {
                        inAnimation = false
                        isExpanded = true
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

                info_menu.visibility = VISIBLE
                newChangeBounds.interpolator = OvershootInterpolator()
                newChangeBounds.duration = 500
                img.visibility = ImageView.GONE

                TransitionManager.beginDelayedTransition(this@FabExpandLayout, newChangeBounds)
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

        val params = this.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER

        this.layoutParams = params

        TransitionManager.beginDelayedTransition(this, changeBounds)
    }

    fun setOnAddClickListener(onClickListener: OnClickListener?) {
        addOnClickListener = onClickListener
    }

    fun setOnCancelClickListener(onClickListener: OnClickListener? = null) {
        cancelOnClickListener = onClickListener
    }

    fun removeAnim() {
        img.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp))
    }

    fun addAnim() {
        img.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_star_border_black_24dp
            )
        )
    }

    override fun onGlobalLayout() {
        val params = img.layoutParams
        defaultParams = params

        text1.text = title

        add.setOnClickListener {
            isWatching = checkbox.isChecked
            if (!isAdded) {
                removeAnim()
            }else {
                addAnim()
            }
            hideDialogExpand()
            addOnClickListener?.onClick(it)

        }

        cancel.setOnClickListener {
            hideDialogExpand()
            cancelOnClickListener?.onClick(it)
        }

        spinner.adapter = ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            android.R.id.text1,
            options
        )

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    checkbox.visibility = CheckBox.VISIBLE
                } else {
                    checkbox.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            android.R.anim.fade_out
                        )
                    )
                    checkbox.visibility = CheckBox.INVISIBLE
                }
            }

        }

        mActivity.onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isExpanded) {
                    hideDialogExpand()
                }else {
                    mActivity.finishAfterTransition()
                }
            }
        })

        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

}
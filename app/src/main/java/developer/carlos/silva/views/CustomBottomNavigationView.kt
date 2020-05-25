package developer.carlos.silva.views

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import com.google.android.material.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.ThemeEnforcement

class CustomBottomNavigationView : BottomNavigationView {

    var inAnimation = false

    constructor(context: Context) : super(context, null)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs, R.attr.bottomNavigationStyle)

    @SuppressLint("RestrictedApi")
    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int
    ) : super(
        ThemeEnforcement.createThemedContext(
            context,
            attrs,
            defStyleAttr, R.style.Widget_Design_BottomNavigationView
        ), attrs, defStyleAttr
    )

    fun show() {
        if (visibility == GONE && !inAnimation) {

            inAnimation = !inAnimation

            val objectAnimator = ObjectAnimator.ofFloat(
                this, "translationY",
                1000f, 0f
            )

            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.duration = 500

            objectAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    inAnimation = false
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })

            objectAnimator.start()

            visibility = VISIBLE

        }
    }

    fun hide() {
        if (visibility == VISIBLE && !inAnimation) {

            inAnimation = !inAnimation

            val objectAnimator = ObjectAnimator.ofFloat(
                this, "translationY",
                0f, 1000f
            )

            objectAnimator.interpolator = AccelerateDecelerateInterpolator()
            objectAnimator.duration = 500

            objectAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    inAnimation = !inAnimation
                    visibility = GONE
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationStart(p0: Animator?) {

                }

            })

            objectAnimator.start()

        }
    }

    fun isShowing() = visibility == BottomNavigationView.VISIBLE

}
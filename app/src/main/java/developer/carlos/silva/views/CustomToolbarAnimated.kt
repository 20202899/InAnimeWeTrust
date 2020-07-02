package developer.carlos.silva.views

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import androidx.appcompat.R
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import developer.carlos.silva.extensions.show

class CustomToolbarAnimated : Toolbar {

    var isShow = false

    constructor(context: Context?) : super(context, null)

    constructor(
        context: Context?,
        attrs: AttributeSet?
    ) : super(context, attrs, R.attr.toolbarStyle)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    fun showWithAnimation() {

        if (isShow)
            return

        val field = this::class.java.superclass.getDeclaredField("mNavButtonView")
        field.isAccessible = true
        val mNavButtonView = field.get(this) as ImageButton
        val objectAnimator = ObjectAnimator.ofFloat(
            mNavButtonView, "translationX",
            -1000f, 0f
        )
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()
        objectAnimator.start()
        isShow = true
    }


    fun hideWithAnimation(): ObjectAnimator? {

        if(!isShow)
            return null

        val field = this::class.java.superclass.getDeclaredField("mNavButtonView")
        field.isAccessible = true
        val mNavButtonView = field.get(this) as ImageButton
        val objectAnimator = ObjectAnimator.ofFloat(
            mNavButtonView, "translationX",
            0f, -1000f
        )
        objectAnimator.interpolator = AccelerateDecelerateInterpolator()

        objectAnimator.start()

        isShow = false

        return objectAnimator
    }

}
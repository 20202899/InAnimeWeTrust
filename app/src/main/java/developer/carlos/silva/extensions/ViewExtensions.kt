package developer.carlos.silva.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

// Toolbar extensions
fun Toolbar.show() {
    val field = this::class.java.getDeclaredField("mNavButtonView")
    field.isAccessible = true
    val mNavButtonView = field.get(this) as ImageButton
    val objectAnimator = ObjectAnimator.ofFloat(mNavButtonView, "translationX",
        -1000f, 0f)
    objectAnimator.interpolator = AccelerateDecelerateInterpolator()

    objectAnimator.start()

}

fun Toolbar.hide(): ObjectAnimator {
    val field = this::class.java.getDeclaredField("mNavButtonView")
    field.isAccessible = true
    val mNavButtonView = field.get(this) as ImageButton
    val objectAnimator = ObjectAnimator.ofFloat(mNavButtonView, "translationX",
        0f, -1000f)
    objectAnimator.interpolator = AccelerateDecelerateInterpolator()

    objectAnimator.start()
    return objectAnimator
}
// Toolbar extensions end

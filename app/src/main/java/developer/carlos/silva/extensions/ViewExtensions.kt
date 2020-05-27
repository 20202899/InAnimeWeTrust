package developer.carlos.silva.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_anime.*

// Toolbar extensions
fun Toolbar.show() {
    val field = this::class.java.getDeclaredField("mNavButtonView")
    field.isAccessible = true
    val mNavButtonView = field.get(this) as ImageButton
    val objectAnimator = ObjectAnimator.ofFloat(
        mNavButtonView, "translationX",
        -1000f, 0f
    )
    objectAnimator.interpolator = AccelerateDecelerateInterpolator()

    objectAnimator.start()

}

fun Toolbar.hide(): ObjectAnimator {
    val field = this::class.java.getDeclaredField("mNavButtonView")
    field.isAccessible = true
    val mNavButtonView = field.get(this) as ImageButton
    val objectAnimator = ObjectAnimator.ofFloat(
        mNavButtonView, "translationX",
        0f, -1000f
    )
    objectAnimator.interpolator = AccelerateDecelerateInterpolator()

    objectAnimator.start()
    return objectAnimator
}
// Toolbar extensions end

//Fab extensions

fun FloatingActionButton.removeAnim() {
    val interpolator = OvershootInterpolator()
    ViewCompat.animate(this).rotation(135f).withLayer().setDuration(300)
        .setInterpolator(interpolator).start()
}

fun FloatingActionButton.addAnim() {
    val interpolator = OvershootInterpolator()
    ViewCompat.animate(this).rotation(0f).withLayer().setDuration(300)
        .setInterpolator(interpolator).start()
}

//Fab end

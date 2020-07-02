package developer.carlos.silva.extensions

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_anime.*
import java.lang.Exception



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

// SearchView extensions

fun SearchView.show() {
    try {
        if (this.visibility == SearchView.GONE) {
            val parent = this.parent
            val slide = Slide(Gravity.START)
            slide.interpolator = OvershootInterpolator()
            slide.duration = 500
            TransitionManager.beginDelayedTransition(parent as ViewGroup, slide)
            this.visibility = SearchView.VISIBLE
        }
    } catch (e: Exception) {

    }
}

//SearchView end


//Activity Extensions
fun Activity.hideSystemUI() {
    // Enables regular immersive mode.
    // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
    // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN)

//    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

}


fun Activity.showSystemUI() {
    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

//    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

}

//Acitivity end
package developer.carlos.silva.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    constructor(context: Context) : super(context)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return false
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return false
    }
}
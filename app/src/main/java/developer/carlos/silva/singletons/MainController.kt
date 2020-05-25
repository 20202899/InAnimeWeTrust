package developer.carlos.silva.singletons

import android.app.Application
import android.os.Handler
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class MainController : Application() {

    private var mQueue: RequestQueue? = null
    lateinit private var mHandler: Handler
    val mGson = Gson()

    companion object {
        private var sInstance: MainController? = null
        fun getInstance() = sInstance
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        mHandler = Handler()
        mQueue = Volley.newRequestQueue(this)
    }

    fun<T> addRequest (request: Request<T>, tag: String) {
        request.tag = tag
        mQueue?.add(request)
    }

    fun cancelRequest(tag: String) {
        mQueue?.cancelAll(tag)
    }

    fun getHandler() = mHandler
}
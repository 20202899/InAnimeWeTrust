package developer.carlos.silva.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BitmapCompat
import com.bumptech.glide.load.resource.bitmap.BitmapDrawableDecoder
import com.bumptech.glide.load.resource.bitmap.BitmapResource
import developer.carlos.silva.R
import developer.carlos.silva.database.DatabaseServices
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.interfaces.AnimeLoaderListener
import developer.carlos.silva.network.LoaderAnimes
import developer.carlos.silva.singletons.MainController

class AnimeNewEpisodeJobService : JobService() {

    companion object {
        const val JOB_SERVICE_NEW_EP_ID = 10
    }

    private val mHandler = Handler()
    private var mJobParameters: JobParameters? = null
    private lateinit var mNotifyManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        mJobParameters = p0
        return true
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        mJobParameters = p0

        startWorkingNotifications()

        return true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_STICKY
    }

    private fun startWorkingNotifications() {
        Thread {
            val db = DatabaseServices.getDataBaseInstance(this)
            val dao = db.dataAnimeDao()
            val notifyMeAnimes = dao.getAllByNotificationFlag()

            notifyMeAnimes.forEach {
                LoaderAnimes.loadAnimes(object : AnimeLoaderListener {
                    override fun onLoad(data: MutableList<Any>) {
                        val a = data[0] as DataAnime

                        if (a.lista.size > it.epsodes.size) {
                            Thread {
                                it.dataAnime.notifyMe = false
                                dao.insertAnime(it.dataAnime)
                                dao.insertEpisode(it.epsodes.last())
                                notification(a)
                            }.start()
                        }

                    }

                }, it.dataAnime.link)
            }

            db.close()

            Thread.sleep(1000)

            if (notifyMeAnimes.none { it.dataAnime.notifyMe }) {
                jobFinished(mJobParameters, false)
            } else {
                jobFinished(mJobParameters, true)
            }


        }.start()
    }

    private fun notification(anime: DataAnime) {
        mHandler.post {
            val lastEp = anime.lista.last()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    "ep_notify",
                    "Novo Episódio",
                    NotificationManager.IMPORTANCE_HIGH
                )
                mNotifyManager.createNotificationChannel(notificationChannel)
            }

            mNotifyManager.notify(anime.id, NotificationCompat.Builder(this, "ep_notify").apply {
                priority = NotificationCompat.PRIORITY_MAX
                setContentTitle(anime.title)
                setContentText("${lastEp.title} disponível.")
                setSmallIcon(R.drawable.otaku2)
                setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("${lastEp.title} disponível.")
                )
                setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.otaku2))
            }.build())

        }
    }

}
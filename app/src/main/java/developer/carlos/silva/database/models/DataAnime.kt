package developer.carlos.silva.database.models

import android.os.Build
import androidx.room.*
import developer.carlos.silva.models.Anime
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
class DataAnime : Serializable {
    var link: String = ""
    var title: String = ""
    var isWatch = false
    var notifyMe = false
    var dateTime: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().toString()
    }else {
        val date = Date()
        val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US)
        simpleDateFormatter.format(date)
    }
    @PrimaryKey
    var id: Int = 0
    var capa: String = ""
    @Ignore
    lateinit var lista: MutableList<DataEpisode>
    @Ignore
    var infos = mutableListOf<String>()
    var sinopse: String = ""
}
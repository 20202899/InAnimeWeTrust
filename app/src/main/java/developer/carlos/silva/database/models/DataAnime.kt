package developer.carlos.silva.database.models

import androidx.room.*
import developer.carlos.silva.models.Anime
import java.io.Serializable

@Entity
class DataAnime : Serializable {
    var link: String = ""
    var title: String = ""
    @PrimaryKey
    var id: Int = 0
    var capa: String = ""
    @Ignore
    lateinit var lista: MutableList<DataEpisode>
    var sinopse: String = ""
}
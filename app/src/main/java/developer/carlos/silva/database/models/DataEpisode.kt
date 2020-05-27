package developer.carlos.silva.database.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class DataEpisode(
    @PrimaryKey
    var id: Int,
    var link: String = "",
    var title: String = "",
    var foreignKey: Int = 0
) : Serializable
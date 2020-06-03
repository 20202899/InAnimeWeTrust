package developer.carlos.silva.database.models

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class AnimeAndEpisodes(
    @Embedded val dataAnime: DataAnime, @Relation(
        parentColumn = "id",
        entityColumn = "foreignKey"
    )
    var epsodes: MutableList<DataEpisode>
) : Serializable
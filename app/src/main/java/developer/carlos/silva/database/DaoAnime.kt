package developer.carlos.silva.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import developer.carlos.silva.database.models.AnimeAndEpisodes
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.database.models.DataEpisode

@Dao
interface DaoAnime {
    @Query("SELECT * FROM DataAnime")
    fun getAll(): MutableList<AnimeAndEpisodes>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAnime(vararg dataAnime: DataAnime)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: List<DataEpisode>)

    @Query("SELECT * FROM DataAnime WHERE id = :id")
    fun isExist(id: Int): DataAnime?

    @Query("DELETE FROM DataAnime WHERE id = :id")
    fun deleteById(id: Int)
}
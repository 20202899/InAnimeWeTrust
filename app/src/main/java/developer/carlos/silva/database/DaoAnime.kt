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
    @Query("SELECT * FROM DataAnime ORDER BY dateTime DESC")
    fun getAll(): MutableList<AnimeAndEpisodes>

    @Query("SELECT * FROM DataAnime WHERE notifyMe = 1")
    fun getAllByNotificationFlag(): MutableList<AnimeAndEpisodes>

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertAnime(vararg dataAnime: DataAnime)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisodes(episode: List<DataEpisode>)

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertEpisode(episode: DataEpisode)

    @Query("SELECT * FROM DataAnime WHERE id = :id")
    fun isExist(id: Int): DataAnime?

    @Query("DELETE FROM DataAnime WHERE id = :id")
    fun deleteById(id: Int)
}
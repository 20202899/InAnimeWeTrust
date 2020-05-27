package developer.carlos.silva.database

import androidx.room.Database
import androidx.room.RoomDatabase
import developer.carlos.silva.database.models.DataAnime
import developer.carlos.silva.database.models.DataEpisode

@Database(entities = [DataAnime::class, DataEpisode::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataAnimeDao(): DaoAnime
}
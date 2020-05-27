package developer.carlos.silva.database

import android.content.Context
import androidx.room.Room

class DatabaseServices {
    companion object {
        fun getDataBaseInstance(context: Context): AppDatabase {
            val db = Room.databaseBuilder(
                context,
                AppDatabase::class.java, "database-anime"
            ).build()
            return db
        }
    }
}
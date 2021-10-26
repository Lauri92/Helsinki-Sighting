package fi.lauriari.helsinkiapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fi.lauriari.helsinkiapp.daos.FavoriteDao
import fi.lauriari.helsinkiapp.entities.Favorite

@Database(
    entities = [(Favorite::class)],
    version = 1,
    exportSchema = false
)
abstract class ItemDB : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDB? = null

        fun getDatabase(context: Context): ItemDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDB::class.java,
                    "item_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
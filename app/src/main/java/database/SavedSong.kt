package database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites_table")
data class SavedSong(
    @PrimaryKey val no: String,
    val title: String,
    val singer: String
)

@Dao
interface SavedSongDao {
    @Query("SELECT * FROM favorites_table")
    fun getAllFavorites(): Flow<List<SavedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(song: SavedSong)

    @Delete
    suspend fun deleteFavorite(song: SavedSong)
}

@Database(entities = [SavedSong::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedSongDao(): SavedSongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "karaoke_favorites_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
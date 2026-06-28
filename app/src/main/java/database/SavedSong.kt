package database

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites_table")
data class SavedSong(
    @PrimaryKey val no: String,
    val title: String,
    val singer: String,
    val release: String,
    val brand: String? = null,
    val composer: String? = null,
    val lyricist: String? = null
)

@Dao
interface SavedSongDao {
    @Query("SELECT * FROM favorites_table")
    fun getAllFavorites(): Flow<List<SavedSong>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(song: SavedSong)

    @Delete
    suspend fun deleteFavorite(song: SavedSong)

    @Query("DELETE FROM favorites_table")
    suspend fun deleteAllFavorites()
}

@Database(entities = [SavedSong::class], version = 3, exportSchema = false)
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
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
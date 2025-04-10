package com.example.zingmp3clone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Callback
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.zingmp3clone.R
import com.example.zingmp3clone.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Song::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zingmp3_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(RoomCallback(context)) // Nếu có khởi tạo sẵn data
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

private class RoomCallback(private val context: Context) : Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            populateDatabase(database.songDao())
        }
    }
}

suspend fun populateDatabase(songDao: SongDao) {
    val sampleSongs = listOf(
        Song(
            id = R.raw.itay,
            name = "I'm Thinking About You",
            artist = "ANH TRAI \"SAY HI\", RHYDER, WEAN, TLINH, ĐỨC PHÚC, HÙNG HUỲNH",
            duration = 391,
            thumbnail = R.drawable.itay
        ),
        Song(
            id = R.raw.kim_phut_kim_gio,
            name = "KIM PHÚT KIM GIỜ",
            artist = "ANH TRAI \"SAY HI\", HIEUTHUHAI, HURRYKNG, NEGAV, PHÁP KIỀU",
            duration = 393,
            thumbnail = R.drawable.kim_phut_kim_gio
        ),
        Song(
            id = R.raw.lan_uu_tien,
            name = "LÀN ƯU TIÊN",
            artist = "MOPIUS",
            duration = 209,
            thumbnail = R.drawable.lan_uu_tien
        ),
        Song(
            id = R.raw.ngao_ngo,
            name = "NGÁO NGƠ",
            artist = "ANH TRAI \"SAY HI\", HIEUTHUHAI, ERIK, ANH TÚ ATUS, JSON",
            duration = 335,
            thumbnail = R.drawable.ngao_ngo
        ),
        Song(
            id = R.raw.quay_di_quay_lai,
            name = "QUAY ĐI QUAY LẠI",
            artist = "ANH TRAI \"SAY HI\", HIEUTHUHAI",
            duration = 292,
            thumbnail = R.drawable.quay_di_quay_lai
        ),
        Song(
            id = R.raw.walk,
            name = "WALK",
            artist = "ANH TRAI \"SAY HI\", HIEUTHUHAI, HURRYKNG, NEGAV, PHÁP KIỀU",
            duration = 349,
            thumbnail = R.drawable.walk
        )
    )

    songDao.insertSongs(sampleSongs)
}
package com.example.flashvocab.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flashvocab.data.db.dao.VocabDao
import com.example.flashvocab.data.db.dao.WordListDao
import com.example.flashvocab.data.db.entity.VocabEntity
import com.example.flashvocab.data.db.entity.WordListEntity

@Database(
    entities = [
        WordListEntity::class,
        VocabEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vocabDao(): VocabDao
    abstract fun wordListDao(): WordListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "flash_vocab_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}

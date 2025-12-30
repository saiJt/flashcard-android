package com.example.flashvocab.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.flashvocab.data.db.entity.WordListEntity

@Dao
interface WordListDao {

    @Query("SELECT * FROM word_lists")
    fun getAllLists(): Flow<List<WordListEntity>>

    @Query("DELETE FROM word_lists WHERE id = :listId")
    suspend fun deleteById(listId: Int)


    @Insert
    suspend fun insert(list: WordListEntity): Long
}

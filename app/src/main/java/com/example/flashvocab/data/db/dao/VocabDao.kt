package com.example.flashvocab.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import com.example.flashvocab.data.db.entity.VocabEntity

@Dao
interface VocabDao {

    // 특정 단어장(listId)에 속한 단어 목록
    @Query("SELECT * FROM vocabs WHERE listId = :listId")
    fun getVocabsByList(listId: Int): Flow<List<VocabEntity>>

    // 단어 추가
    @Insert
    suspend fun insert(vocab: VocabEntity)

    // Know / Don't know 기록 업데이트
    @Update
    suspend fun update(vocab: VocabEntity)

    @Delete
    suspend fun delete(vocab: VocabEntity)
}
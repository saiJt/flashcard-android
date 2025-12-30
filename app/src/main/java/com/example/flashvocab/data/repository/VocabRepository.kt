package com.example.flashvocab.data.repository

import com.example.flashvocab.data.db.dao.VocabDao
import com.example.flashvocab.data.db.dao.WordListDao
import com.example.flashvocab.data.db.entity.VocabEntity
import com.example.flashvocab.data.db.entity.WordListEntity
import kotlinx.coroutines.flow.Flow

class VocabRepository(
    private val vocabDao: VocabDao,
    private val wordListDao: WordListDao
) {

    // 모든 단어장
    fun getAllWordLists(): Flow<List<WordListEntity>> =
        wordListDao.getAllLists()

    // 특정 단어장의 단어들
    fun getVocabsByList(listId: Int): Flow<List<VocabEntity>> =
        vocabDao.getVocabsByList(listId)

    // 단어장 추가
    suspend fun insertWordList(name: String): Int {
        return wordListDao.insert(
            WordListEntity(name = name)
        ).toInt()
    }

    // 단어 추가
    suspend fun insertVocab(
        front: String,
        back: String,
        listId: Int
    ) {
        vocabDao.insert(
            VocabEntity(
                front = front,
                back = back,
                listId = listId
            )
        )
    }

    // 단어 삭제
    suspend fun deleteVocab(vocab: VocabEntity) {
        vocabDao.delete(vocab)
    }

    // Know
    suspend fun markKnow(vocab: VocabEntity) {
        vocabDao.update(
            vocab.copy(knowCount = vocab.knowCount + 1)
        )
    }

    // Don't know
    suspend fun markDontKnow(vocab: VocabEntity) {
        vocabDao.update(
            vocab.copy(dontKnowCount = vocab.dontKnowCount + 1)
        )
    }

    // 단어장 삭제
    suspend fun deleteWordList(listId: Int) {
        wordListDao.deleteById(listId)
    }

}

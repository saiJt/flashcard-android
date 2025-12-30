package com.example.flashvocab.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashvocab.data.db.AppDatabase
import com.example.flashvocab.data.repository.VocabRepository
import com.example.flashvocab.data.db.entity.VocabEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FlashVocabViewModel(application: Application) :
    AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = VocabRepository(
        db.vocabDao(),
        db.wordListDao()
    )

    // 선택된 단어장
    private val selectedListId = MutableStateFlow<Int?>(null)

    // 단어장 목록
    val wordLists = repository.getAllWordLists()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // 선택된 단어장에 따른 단어 목록
    val vocabs = selectedListId.flatMapLatest { listId ->
        listId?.let {
            repository.getVocabsByList(it)
        } ?: flowOf(emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun selectList(id: Int) {
        selectedListId.value = id
    }

    fun addTestData() {
        viewModelScope.launch {
            val listId = repository.insertWordList("TEST LIST")
            repository.insertVocab("apple", "사과", listId)
            repository.insertVocab("banana", "바나나", listId)
        }
    }

    fun know(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.markKnow(vocab)
        }
    }

    fun dontKnow(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.markDontKnow(vocab)
        }
    }

    fun createWordList(name: String, onDone: (Int) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertWordList(name)
            onDone(id)
        }
    }

    // 단어 추가
    fun addVocab(listId: Int, front: String, back: String) {
        viewModelScope.launch {
            repository.insertVocab(
                front = front,
                back = back,
                listId = listId
            )
        }
    }

    fun deleteVocab(vocab: VocabEntity) {
        viewModelScope.launch {
            repository.deleteVocab(vocab)
        }
    }


    // 단어장 삭제
    fun deleteWordList(listId: Int) {
        viewModelScope.launch {
            repository.deleteWordList(listId)
        }
    }



}

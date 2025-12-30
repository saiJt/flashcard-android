package com.example.flashvocab.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_lists")
data class WordListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
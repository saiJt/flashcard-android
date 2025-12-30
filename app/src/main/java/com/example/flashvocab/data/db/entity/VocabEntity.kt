package com.example.flashvocab.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabs",
    foreignKeys = [
        ForeignKey(
            entity = WordListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VocabEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val front: String,
    val back: String,
    val listId: Int,
    val knowCount: Int = 0,
    val dontKnowCount: Int = 0
)
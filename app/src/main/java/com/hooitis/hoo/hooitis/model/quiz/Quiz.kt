package com.hooitis.hoo.hooitis.model.quiz

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
data class Quiz(
        @PrimaryKey(autoGenerate = false)
        val id: Long = 0,
        val answerList: List<String>,
        val imageUrl: String
)
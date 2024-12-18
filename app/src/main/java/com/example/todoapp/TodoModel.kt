package com.example.todoapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoModel(
    var title: String,
    var description: String,
    var category: String,
    var date: Long,
    var time: Long,
    var tasks: String = "",       // For checked tasks only
    var checkboxes: String = "",   // For all checkboxes
    var isFinished: Int = 0,
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
)


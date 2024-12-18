package com.example.todoapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import androidx.room.Update

@Dao
interface TodoDao {

    @Insert
    suspend fun insertTask(todoModel: TodoModel): Long

    @Query("SELECT * FROM TodoModel WHERE isFinished = 0")
    fun getTask(): LiveData<List<TodoModel>>

    @Query("Update TodoModel Set isFinished = 1 where id=:uid")
    fun finishTask(uid: Long)

    @Query("Delete from TodoModel where id=:uid")
    fun deleteTask(uid: Long)

    @Query("SELECT * FROM TodoModel WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Long): TodoModel


    @Query("DELETE FROM TodoModel WHERE isFinished = 1")
    suspend fun clearHistory()

    @Query("SELECT * FROM TodoModel WHERE isFinished = 1")
    fun getFinishedTasks(): LiveData<List<TodoModel>>



    @Update
    suspend fun updateTask(todoModel: TodoModel)  // Add this method
}

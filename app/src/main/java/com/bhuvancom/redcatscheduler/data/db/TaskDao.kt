package com.bhuvancom.redcatscheduler.data.db

import androidx.paging.PagingSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   08:53 PM
Project Redcat Scheduler
 */
@Dao
interface TaskDao {
    @Insert(onConflict = REPLACE)
    suspend fun upsertTask(task: Task): Long

    @Insert(onConflict = REPLACE)
    suspend fun upsertTaskExecution(taskExecution: TaskExecution): Long

    @Query("SELECT * FROM Task order by taskId desc")
    fun getTaskList(): PagingSource<Int, Task>

    @Transaction
    @Query("SELECT * FROM Task where taskId=:id")
    suspend fun getTask(id: Int): Task?

    @Transaction
    @Query("SELECT * FROM TaskExecution where taskId=:taskId")
    fun getTaskExecutionsOfTask(taskId: Int): PagingSource<Int, TaskExecution>

    @Delete(entity = Task::class)
    suspend fun delete(task: Task)
}
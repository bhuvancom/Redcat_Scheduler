package com.bhuvancom.redcatscheduler.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
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
    suspend fun upsertTask(task: Task)

    @Insert(onConflict = REPLACE)
    suspend fun upsertTaskExecution(taskExecution: TaskExecution)

    @Query("SELECT * FROM Task order by taskId desc")
    fun getTaskList(): PagingSource<Int, Task>

    @Transaction
    @Query("SELECT * FROM TASK where taskId=:id")
    suspend fun getTask(id: Int): Task?

    @Transaction
    @Query("SELECT * FROM TaskExecution where taskId=:taskId")
    fun getTaskExecutionsOfTask(taskId: Int): PagingSource<Int, TaskExecution>
}
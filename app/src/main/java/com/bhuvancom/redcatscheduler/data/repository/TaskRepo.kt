package com.bhuvancom.redcatscheduler.data.repository

import androidx.paging.PagingSource
import com.bhuvancom.redcatscheduler.data.db.TaskDao
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution
import javax.inject.Inject

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:18 PM
Project Redcat Scheduler
 */
class TaskRepo @Inject constructor(private val taskDao: TaskDao) {
    fun getTasks(): PagingSource<Int, Task> = taskDao.getTaskList()
    suspend fun getTask(taskId: Int): Task? = taskDao.getTask(taskId)
    fun getTaskExecutions(taskId: Int) =
        taskDao.getTaskExecutionsOfTask(taskId)

    suspend fun save(it: Task) = taskDao.upsertTask(it)


    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }
}
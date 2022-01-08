package com.bhuvancom.redcatscheduler.data.repository

import com.bhuvancom.redcatscheduler.data.db.TaskDao
import javax.inject.Inject

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:18 PM
Project Redcat Scheduler
 */
class TaskRepo @Inject constructor(private val taskDao: TaskDao) {
    fun getTasks() = taskDao.getTaskList()
}
package com.bhuvancom.redcatscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   08:58 PM
Project Redcat Scheduler
 */
@Entity
data class TaskExecution(
    @PrimaryKey(autoGenerate = true)
    val executionId: Int? = null,
    val task: Task
)
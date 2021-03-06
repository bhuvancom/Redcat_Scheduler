package com.bhuvancom.redcatscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bhuvancom.redcatscheduler.data.model.TaskType.TaskType
import java.util.*

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   08:53 PM
Project Redcat Scheduler
 */
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int? = null,
    val taskName: String = "",
    val taskPriority: TaskPriority = TaskPriority.HIGH,
    val taskType: TaskType = TaskType.NORMAL,
    val taskTime: Date = Date(),
    val createdOn: Date = Date(),
    val updatedOn: Date = Date(),
    val data: String = "",
    val isRepeating: Boolean = false,
)
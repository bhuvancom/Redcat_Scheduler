package com.bhuvancom.redcatscheduler.data.model.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.bhuvancom.redcatscheduler.data.model.Task

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:52 PM
Project Redcat Scheduler
 */
data class TaskWithTaskExecutions(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "taskId"
    )
    val taskExecutions: List<TaskWithTaskExecutions> = listOf()
)
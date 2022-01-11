package com.bhuvancom.redcatscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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
    val taskId: Int,
    val isSuccess: Boolean,
    val executionTime: Date = Date(),
    val msg: String = "",
)
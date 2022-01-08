package com.bhuvancom.redcatscheduler.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   08:53 PM
Project Redcat Scheduler
 */
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

)
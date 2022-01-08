package com.bhuvancom.redcatscheduler.data.model

import androidx.room.TypeConverter
import java.util.*

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:06 PM
Project Redcat Scheduler
 */
class DateConvertor {
    @TypeConverter
    fun toDate(timeStamp: Long): Date = Date(timeStamp)

    @TypeConverter
    fun toTimeStamp(date: Date): Long = date.time
}
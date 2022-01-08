package com.bhuvancom.redcatscheduler.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bhuvancom.redcatscheduler.data.model.DateConvertor
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   08:53 PM
Project Redcat Scheduler
 */
@Database(entities = [Task::class, TaskExecution::class], version = 1)
@TypeConverters(DateConvertor::class)
abstract class SchedulerDatabase : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao

    companion object {
        @Volatile
        private var instance: SchedulerDatabase? = null
        fun getDatabase(context: Context): SchedulerDatabase {
            val tempInstance = instance
            if (tempInstance != null) return tempInstance
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    SchedulerDatabase::class.java,
                    SchedulerDatabase::class.java.simpleName
                ).build()
                this.instance = instance
                return this.instance!!
            }
        }

    }
}
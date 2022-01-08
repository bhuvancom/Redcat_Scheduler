package com.bhuvancom.redcatscheduler.data.di

import android.content.Context
import com.bhuvancom.redcatscheduler.data.db.SchedulerDatabase
import com.bhuvancom.redcatscheduler.data.db.TaskDao
import com.bhuvancom.redcatscheduler.data.repository.TaskRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:16 PM
Project Redcat Scheduler
 */
@Module
@InstallIn(SingletonComponent::class)
object ApplicationBeans {
    @Singleton
    @Provides
    fun appDb(@ApplicationContext context: Context): SchedulerDatabase =
        SchedulerDatabase.getDatabase(context)

    @Provides
    @Singleton
    fun providesDao(schedulerDatabase: SchedulerDatabase): TaskDao = schedulerDatabase.getTaskDao()

    @Provides
    @Singleton
    fun provideRepo(taskDao: TaskDao): TaskRepo = TaskRepo(taskDao)
}
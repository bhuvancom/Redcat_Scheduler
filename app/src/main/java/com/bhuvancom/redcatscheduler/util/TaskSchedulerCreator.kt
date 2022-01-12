package com.bhuvancom.redcatscheduler.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.services.TaskService

/**
@author Bhuvaneshvar
Date    10-Jan-2022
Time   12:34 AM
Project Redcat Scheduler
 */
class TaskSchedulerCreator(private val context: Context) {
    fun createTask(task: Task) {
        val component = ComponentName(context, TaskService::class.java)
        val job = JobInfo.Builder(task.taskId ?: 0, component).apply {
            setExtras(PersistableBundle().apply {
                putInt(Constants.KEY_TASK_ID, task.taskId ?: -1)
            })
            if (task.isRepeating) {
                this.setPeriodic(24 * 60 * 60 * 1000)
            }
            setRequiresDeviceIdle(false)
            setMinimumLatency(1)
            setRequiresCharging(false)
        }
        context.getSystemService(JobScheduler::class.java).schedule(job.build())
    }

    fun create(task: Task) {
        val intent = Intent(context, TaskScheduler::class.java)
        intent.putExtra("taskId", task.taskId ?: -1)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            0
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (task.isRepeating)
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                task.taskTime.time,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        else alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            task.taskTime.time,
            pendingIntent
        )

        Log.d(TAG, "create: $task")
    }

    companion object {
        private const val TAG = "TaskSchedulerCreator"
    }
}
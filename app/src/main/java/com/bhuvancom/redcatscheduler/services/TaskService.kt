package com.bhuvancom.redcatscheduler.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.util.Log
import com.bhuvancom.redcatscheduler.data.db.SchedulerDatabase
import com.bhuvancom.redcatscheduler.data.model.NotificationItem.NotificationItemModel
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution
import com.bhuvancom.redcatscheduler.data.model.TaskType.TaskType
import com.bhuvancom.redcatscheduler.util.Constants
import com.bhuvancom.redcatscheduler.util.NotificationUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
@author Bhuvaneshvar
Date    11-Jan-2022
Time   08:43 PM
Project Redcat Scheduler
 */
class TaskService : JobService() {
    companion object {
        private const val TAG = "TaskService"
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val taskId = params?.extras?.getInt(Constants.KEY_TASK_ID, -1) ?: -1
        CoroutineScope(Dispatchers.Default).launch {
            runTask(applicationContext, taskId)
        }
        return true
    }

    suspend fun runTask(context: Context, taskId: Int = -1) {
        val taskDao = SchedulerDatabase.getDatabase(context).getTaskDao()
        try {
            val taskData: Task? = taskDao.getTask(taskId)
            Log.d(TAG, "doWork: task $taskData")
            taskData?.let { task ->

                if (task.taskType == TaskType.HTTP) {
                    var msg = ""
                    val url = URL(task.data)
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "GET"  // optional default is GET
                        msg += "Response $responseCode"
                        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                    }
                    val taskExecution = TaskExecution(
                        taskId = taskId,
                        isSuccess = true,
                        msg = msg
                    )
                    taskDao.upsertTaskExecution(taskExecution)
                    NotificationUtil.notify(NotificationItemModel(title = task.taskName,
                        subTitle = task.data),
                        context = context)
                } else {
                    val taskExecution = TaskExecution(
                        taskId = taskId,
                        isSuccess = true,
                        msg = "Notified"
                    )
                    taskDao.upsertTaskExecution(taskExecution)
                    NotificationUtil.notify(NotificationItemModel(title = task.taskName,
                        subTitle = task.data),
                        context = context)
                }
            }
        } catch (e: Exception) {
            val task: Task? = taskDao.getTask(taskId)
            task?.let { task ->
                val taskExecution = TaskExecution(
                    taskId = taskId,
                    isSuccess = true,
                    msg = "Failed due to ${e.message}"
                )
                taskDao.upsertTaskExecution(taskExecution)
                NotificationUtil.notify(NotificationItemModel(title = "task failed " + task.taskName,
                    subTitle = task.data),
                    context = context)
            }
            Log.e(TAG, "doWork: error in work ", e)
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return true
    }
}
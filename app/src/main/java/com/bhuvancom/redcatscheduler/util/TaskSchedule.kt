package com.bhuvancom.redcatscheduler.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bhuvancom.redcatscheduler.data.db.SchedulerDatabase
import com.bhuvancom.redcatscheduler.data.model.NotificationItem.NotificationItemModel
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution
import com.bhuvancom.redcatscheduler.data.model.TaskType.TaskType
import java.net.HttpURLConnection
import java.net.URL

/**
@author Bhuvaneshvar
Date    09-Jan-2022
Time   05:23 PM
Project Redcat Scheduler
 */
class TaskSchedule(appContext: Context, workParam: WorkerParameters) :
    CoroutineWorker(appContext, workParam) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        val taskDao = SchedulerDatabase.getDatabase(appContext).getTaskDao()
        val taskId = inputData.getInt(Constants.KEY_TASK_ID, -1)
        return try {
            val taskData: Task? = taskDao.getTask(taskId)
            Log.d(TAG, "doWork: task $taskData")

            taskData?.let { task ->
                val systemTime = System.currentTimeMillis()
                val taskTime = task.taskTime.time
                if (taskTime - systemTime < 1) return@let

                if (task.taskType == TaskType.HTTP) {
                    var msg = ""
                    val url = URL(task.data)
                    with(url.openConnection() as HttpURLConnection) {
                        requestMethod = "GET"  // optional default is GET
                        msg += "Response $responseCode"
                        println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")
                        inputStream.bufferedReader().use {
                            it.lines().forEach { line ->
                                println(line)
                            }
                        }
                    }
                    val taskExecution = TaskExecution(
                        taskId = taskId,
                        isSuccess = true,
                        msg = msg
                    )
                    taskDao.upsertTaskExecution(taskExecution)
                    NotificationUtil.notify(NotificationItemModel(title = task.taskName,
                        subTitle = task.data),
                        context = appContext)
                } else {
                    val taskExecution = TaskExecution(
                        taskId = taskId,
                        isSuccess = true,
                        msg = "Notified"
                    )
                    taskDao.upsertTaskExecution(taskExecution)
                    NotificationUtil.notify(NotificationItemModel(title = task.taskName,
                        subTitle = task.data),
                        context = appContext)
                }
            }
            Result.success()
        } catch (e: Exception) {
            val task: Task? = taskDao.getTask(taskId)
            task?.let { task ->
                val taskExecution = TaskExecution(
                    taskId = taskId,
                    isSuccess = true,
                    msg = "Failed due to ${e.message}"
                )
                NotificationUtil.notify(NotificationItemModel(title = "task failed " + task.taskName,
                    subTitle = task.data),
                    context = appContext)
            }

            Log.e(TAG, "doWork: error in work ", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "TaskSchedule"
    }
}
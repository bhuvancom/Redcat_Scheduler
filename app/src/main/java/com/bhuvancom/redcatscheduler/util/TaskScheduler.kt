package com.bhuvancom.redcatscheduler.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bhuvancom.redcatscheduler.data.db.SchedulerDatabase
import com.bhuvancom.redcatscheduler.data.model.NotificationItem.NotificationItemModel
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.model.TaskExecution
import com.bhuvancom.redcatscheduler.data.model.TaskType.TaskType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
@author Bhuvaneshvar
Date    10-Jan-2022
Time   12:28 AM
Project Redcat Scheduler
 */
class TaskScheduler : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive: called")
        context ?: return
        val taskId = intent?.getIntExtra("taskId", -1) ?: -1
        CoroutineScope(Dispatchers.Default).launch {
            runTask(context, taskId)
        }
    }

    companion object {
        private const val TAG = "TaskScheduler"
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
    }
}
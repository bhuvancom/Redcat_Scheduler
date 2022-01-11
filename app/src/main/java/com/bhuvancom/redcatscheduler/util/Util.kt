package com.bhuvancom.redcatscheduler.util

import android.content.Context
import android.widget.Toast
import com.bhuvancom.redcatscheduler.data.model.TaskPriority
import com.bhuvancom.redcatscheduler.data.model.TaskType.TaskType
import java.text.SimpleDateFormat
import java.util.*

object Util {
    fun taskPriority(priority: String): TaskPriority = when (priority.lowercase()) {
        "MEDIUM".lowercase() -> TaskPriority.MEDIUM
        "LOW".lowercase() -> TaskPriority.LOW
        else -> TaskPriority.HIGH
    }

    fun taskType(taskType: String): TaskType = when (taskType.lowercase()) {
        "HTTP".lowercase() -> TaskType.HTTP
        else -> TaskType.NORMAL
    }

    fun Context.showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun Date.sdf(): String {
        return SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.getDefault()).format(this)
    }


    /**
     * @param id id of string array
     * @param str string to find
     * @param context context
     */
    fun indexOf(id: Int, str: String, context: Context): Int {
        return try {
            context.resources.getStringArray(id)
                .indexOf(str)
        } catch (e: Exception) {
            -1
        }
    }
}
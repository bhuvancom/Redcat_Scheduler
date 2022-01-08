package com.bhuvancom.redcatscheduler.ui.diff_utils

import androidx.recyclerview.widget.DiffUtil
import com.bhuvancom.redcatscheduler.data.model.Task

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   10:29 PM
Project Redcat Scheduler
 */
class TaskDiffUtil : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
        oldItem.taskId == newItem.taskId

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
}
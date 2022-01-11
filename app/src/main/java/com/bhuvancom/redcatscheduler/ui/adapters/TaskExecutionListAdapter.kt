package com.bhuvancom.redcatscheduler.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bhuvancom.redcatscheduler.data.model.TaskExecution
import com.bhuvancom.redcatscheduler.databinding.ItemTaskExecutionBinding
import com.bhuvancom.redcatscheduler.util.Util.sdf

/**
@author Bhuvaneshvar
Date    09-Jan-2022
Time   12:14 AM
Project Redcat Scheduler
 */
class TaskExecutionListAdapter :
    PagingDataAdapter<TaskExecution, TaskExecutionListAdapter.TaskExecutionVH>(
        object : DiffUtil.ItemCallback<TaskExecution>() {
            override fun areItemsTheSame(oldItem: TaskExecution, newItem: TaskExecution): Boolean {
                return oldItem.taskId == newItem.taskId
                        && oldItem.executionId == newItem.executionId
                        && oldItem.isSuccess == newItem.isSuccess
            }

            override fun areContentsTheSame(
                oldItem: TaskExecution,
                newItem: TaskExecution,
            ): Boolean = oldItem == newItem
        }
    ) {
    inner class TaskExecutionVH(private val binding: ItemTaskExecutionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(taskExecution: TaskExecution) {
            binding.apply {
                tvTaskMsg.text = taskExecution.msg
                tvTaskTime.text = taskExecution.executionTime.sdf()
                tvTaskWasSuccess.text =
                    if (taskExecution.isSuccess) "Successfully ran" else "Failed in run"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskExecutionVH {
        return TaskExecutionVH(
            ItemTaskExecutionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TaskExecutionVH, position: Int) {
        val data = getItem(position)
        data?.let { holder.bind(it) }
    }

    companion object {
        private const val TAG = "TaskExecutionListAdapte"
    }
}
package com.bhuvancom.redcatscheduler.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.databinding.ItemTaskBinding
import com.bhuvancom.redcatscheduler.ui.diff_utils.TaskDiffUtil
import com.bhuvancom.redcatscheduler.util.Util.sdf

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   10:27 PM
Project Redcat Scheduler
 */
class TaskListAdapter(
    private inline val onTaskEdit: (Int) -> Unit,
    private inline val onTaskClick: (Int) -> Unit,
) :
    PagingDataAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffUtil()) {
    fun getItemAt(position: Int): Task? = getItem(position)
    inner class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private inline val onEditClick: (Int) -> Unit,
        private inline val onTaskClick: (Int) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvEdit.setOnClickListener {
                onEditClick(bindingAdapterPosition)
            }
            binding.root.setOnClickListener {
                onTaskClick.invoke(bindingAdapterPosition)
            }
        }

        fun bind(task: Task) {
            binding.apply {
                val cont = root.context
                tvTaskTitle.text = task.taskName
                tvTaskData.text = task.data
                tvTaskPriority.text = cont.getString(R.string.task_priority, task.taskPriority)
                tvTaskType.text = cont.getString(R.string.task_type, task.taskType)
                tvLastUpdated.text = cont.getString(R.string.last_updated, task.updatedOn.sdf())
                val repeat = if (task.isRepeating) " (Repeating)" else " (one time)"
                tvLastRun.text = cont.getString(R.string.task_timer, task.taskTime.sdf() + repeat)
                tvCreatedOn.text = cont.getString(R.string.task_created_on, task.createdOn.sdf())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onEditClick = {
                getItem(it)?.let { task ->
                    task.taskId?.let(onTaskEdit)
                }
            }
        ) {
            getItem(it)?.let { task ->
                task.taskId?.let(onTaskClick)
            }
        }
    }

    override fun onBindViewHolder(holder: TaskListAdapter.TaskViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
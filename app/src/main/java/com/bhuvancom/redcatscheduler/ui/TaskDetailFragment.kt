package com.bhuvancom.redcatscheduler.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.databinding.FragmentTaskDetailsBinding
import com.bhuvancom.redcatscheduler.ui.adapters.TaskExecutionListAdapter
import com.bhuvancom.redcatscheduler.util.Util.sdf
import com.bhuvancom.redcatscheduler.viewModel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   10:07 PM
Project Redcat Scheduler
 */
@AndroidEntryPoint
class TaskDetailFragment : Fragment(R.layout.fragment_task_details) {
    private var _bind: FragmentTaskDetailsBinding? = null
    private val binding get() = _bind!!

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }

    private val args by navArgs<TaskDetailFragmentArgs>()
    private val taskExecutionAdapter = TaskExecutionListAdapter()


    companion object {
        private const val TAG = "TaskDetailFragment"
    }

    private val taskViewModel by viewModels<TaskViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentTaskDetailsBinding.bind(view)
        setupView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setupView() {
        binding.apply {
            taskViewModel.getTaskById(args.taskId) { task ->
                task?.let { res ->
                    binding.taskDetails.apply {
                        val cont = requireContext()
                        tvTaskTitle.text = task.taskName
                        tvTaskData.text = task.data
                        tvTaskPriority.text =
                            cont.getString(R.string.task_priority, task.taskPriority)
                        tvTaskType.text = cont.getString(R.string.task_type, task.taskType)
                        tvLastUpdated.text =
                            cont.getString(R.string.last_updated, task.updatedOn.sdf())
                        val repeat = if (task.isRepeating) " (Repeating)" else " (one time)"
                        tvLastRun.text =
                            cont.getString(R.string.task_timer, task.taskTime.sdf() + repeat)
                        tvCreatedOn.text =
                            cont.getString(R.string.task_created_on, task.createdOn.sdf())
                    }
                }
            }

        }
        lifecycleScope.launch {
            taskViewModel.taskExecutions(args.taskId).collect { data ->
                data.map {
                    Log.d(TAG, "setupView: $it")
                }
                taskExecutionAdapter.submitData(data)
            }
        }

        binding.rvTaskExecutions.apply {
            adapter = taskExecutionAdapter
//            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
}
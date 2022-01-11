package com.bhuvancom.redcatscheduler.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.databinding.FragmentTaskListBinding
import com.bhuvancom.redcatscheduler.ui.adapters.LoadStateAdapter
import com.bhuvancom.redcatscheduler.ui.adapters.TaskListAdapter
import com.bhuvancom.redcatscheduler.util.TaskSchedule
import com.bhuvancom.redcatscheduler.util.TaskSchedulerCreator
import com.bhuvancom.redcatscheduler.viewModel.TaskViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:21 PM
Project Redcat Scheduler
 */
@AndroidEntryPoint
open class TaskListFragment : Fragment(R.layout.fragment_task_list) {
    private var _bind: FragmentTaskListBinding? = null
    private val binding get() = _bind!!
    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }

    private val taskViewModel by viewModels<TaskViewModel>()

    private val taskListAdapter by lazy {
        TaskListAdapter(onTaskEdit = { taskId ->
            // todo open add bottom sheet with task id
            val actionTaskListFragmentToAddNewTask: NavDirections =
                TaskListFragmentDirections.actionTaskListFragmentToAddNewTask(taskId)
            findNavController().navigate(actionTaskListFragmentToAddNewTask)
        }) { taskId ->
            // todo open task details fragment passing this task id
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToTaskDetailFragment(taskId)
            )
        }
    }

    private fun setupView() {
        binding.apply {
            btnAdd.setOnClickListener {
                // todo open add bottom sheet
                val actionTaskListFragmentToAddNewTask: NavDirections =
                    TaskListFragmentDirections.actionTaskListFragmentToAddNewTask()
                findNavController().navigate(actionTaskListFragmentToAddNewTask)
            }

            rvTasks.apply {
                adapter = taskListAdapter.withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { taskListAdapter.retry() },
                    footer = LoadStateAdapter { taskListAdapter.retry() },
                )
                layoutManager = LinearLayoutManager(context)
//                setHasFixedSize(true)
            }
            commonLay.btnRetry.setOnClickListener {
                taskListAdapter.retry()
            }

            val simpleCallback: ItemTouchHelper.SimpleCallback = object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder,
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val pos = viewHolder.absoluteAdapterPosition
                    taskListAdapter.getItemAt(pos)?.run {
                        taskViewModel.deleteBy(this)
                        val workM = WorkManager.getInstance(requireContext())
                        workM.cancelAllWorkByTag(taskId.toString())
                    }

                    taskListAdapter.getItemAt(pos)?.let { task ->
                        val make: Snackbar = Snackbar.make(binding.root,
                            getString(R.string.deleted),
                            Snackbar.LENGTH_LONG)

                        make.setActionTextColor(resources.getColor(R.color.teal_200))

                        make.setAction(getString(R.string.undo)) {
                            taskViewModel.saveTask(task) {
                                val dataInput = Data.Builder()
                                    .putInt("taskId", it.toInt())
                                    .build()
                                val currentTime = System.currentTimeMillis()
                                val taskTime = task.taskTime.time
                                val delay = taskTime - currentTime
                                TaskSchedulerCreator(requireContext()).create(task.copy(taskId = it.toInt()))
                                /*
                                if (task.isRepeating && delay > 0) {
                                    val work = PeriodicWorkRequestBuilder<TaskSchedule>(24,
                                        TimeUnit.HOURS,
                                        delay,
                                        TimeUnit.MILLISECONDS)
                                        .addTag(task.taskId.toString())
                                        .setInputData(dataInput)
                                        .build()
                                    val workM =
                                        WorkManager.getInstance(requireContext()).enqueue(work)

                                } else {
                                    if (delay < 1) return@saveTask

                                    val work = OneTimeWorkRequestBuilder<TaskSchedule>()
                                        .addTag(task.taskId.toString())
                                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                                        .setInputData(dataInput)
                                        .build()
                                    val workM =
                                        WorkManager.getInstance(requireContext()).enqueue(work)
                                }
                                */
                            }
                            binding.rvTasks.smoothScrollToPosition(pos)
                        }
                        make.show()
                    }
                }
            }
            val helper = ItemTouchHelper(simpleCallback)
            helper.attachToRecyclerView(binding.rvTasks)
        }

        taskListAdapter.addLoadStateListener { combinedLoadStates: CombinedLoadStates ->
            binding.apply {
                root.isVisible = true
                commonLay.apply {
                    progressBar.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                    tvNoResult.isVisible =
                        combinedLoadStates.source.refresh is LoadState.NotLoading &&
                                combinedLoadStates.append.endOfPaginationReached && taskListAdapter.itemCount < 1

                    tvError.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                    btnRetry.isVisible = tvError.isVisible
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _bind = FragmentTaskListBinding.bind(view)
        setupView()
        lifecycleScope.launch {
            taskViewModel.tasks.collectLatest { data ->
                taskListAdapter.submitData(data)
            }
        }
    }

    companion object {
        private const val TAG = "TaskListFragment"
    }
}
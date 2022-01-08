package com.bhuvancom.redcatscheduler.ui

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhuvancom.redcatscheduler.databinding.FragmentTaskListBinding
import com.bhuvancom.redcatscheduler.ui.adapters.LoadStateAdapter
import com.bhuvancom.redcatscheduler.ui.adapters.TaskListAdapter
import com.bhuvancom.redcatscheduler.viewModel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:21 PM
Project Redcat Scheduler
 */
@AndroidEntryPoint
open class TaskListFragment : BaseFragment<FragmentTaskListBinding>() {
    override fun getViewBinding(): FragmentTaskListBinding =
        FragmentTaskListBinding.inflate(layoutInflater)

    private val taskListAdapter by lazy {
        TaskListAdapter(onTaskEdit = { taskId ->
            // todo open add bottom sheet with task id
        }) { taskId ->
            // todo open task details fragment passing this task id

        }
    }

    private val taskViewModel by viewModels<TaskViewModel>()

    override fun setupView() {
        binding.apply {
            btnAdd.setOnClickListener {
                // todo open add bottom sheet
            }

            rvTasks.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                adapter = taskListAdapter.withLoadStateHeaderAndFooter(
                    header = LoadStateAdapter { taskListAdapter.retry() },
                    footer = LoadStateAdapter { taskListAdapter.retry() },
                )
            }
            commonLay.btnRetry.setOnClickListener {
                taskListAdapter.retry()
            }

        }

        lifecycleScope.launchWhenCreated {
            taskViewModel.task.collectLatest {
                taskListAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }

        taskListAdapter.addLoadStateListener { combinedLoadStates: CombinedLoadStates ->
            binding.apply {
                commonLay.apply {
                    progressBar.isVisible = combinedLoadStates.source.refresh is LoadState.Loading
                    tvNoResult.isVisible =
                        combinedLoadStates.source.refresh is LoadState.NotLoading &&
                                combinedLoadStates.append.endOfPaginationReached && taskListAdapter.itemCount < 1
                    rvTasks.isVisible = !tvNoResult.isVisible
                    tvError.isVisible = combinedLoadStates.source.refresh is LoadState.Error
                    btnRetry.isVisible = tvError.isVisible
                }
            }
        }
    }
}
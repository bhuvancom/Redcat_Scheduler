package com.bhuvancom.redcatscheduler.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.data.repository.TaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:20 PM
Project Redcat Scheduler
 */
@HiltViewModel
class TaskViewModel @Inject constructor(private val repo: TaskRepo) : ViewModel() {
    val task = MutableLiveData<Task>()
    suspend fun getTask(taskId: Int) = repo.getTask(taskId)
    private var listener: (String) -> Unit = {

    }
    fun taskExecutions(taskId: Int) = Pager(
        PagingConfig(
            pageSize = 10, enablePlaceholders = true
        ),
        pagingSourceFactory = {
            repo.getTaskExecutions(taskId)
        }
    ).flow

    fun saveTask(it: Task, taskId: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repo.save(it)
            taskId.invoke(id)
        }
    }

    fun getTaskById(taskId: Int, result: (Task?) -> Unit) {
        viewModelScope.launch {
            repo.getTask(taskId)?.let(result)
        }
    }

    fun deleteBy(task: Task) {
        viewModelScope.launch {
            repo
                .delete(task)
        }
    }

    val tasks = Pager(
        PagingConfig(pageSize = 10, enablePlaceholders = true,),
    ) {
        repo.getTasks()
    }.flow
}
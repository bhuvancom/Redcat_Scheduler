package com.bhuvancom.redcatscheduler.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.bhuvancom.redcatscheduler.data.repository.TaskRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:20 PM
Project Redcat Scheduler
 */
@HiltViewModel
class TaskViewModel @Inject constructor(private val repo: TaskRepo) : ViewModel() {
    val task = Pager(
        PagingConfig(pageSize = 10, enablePlaceholders = true),
    ) {
        repo.getTasks()
    }.flow.cachedIn(viewModelScope)
}
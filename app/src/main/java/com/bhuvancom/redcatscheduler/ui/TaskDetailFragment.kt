package com.bhuvancom.redcatscheduler.ui

import com.bhuvancom.redcatscheduler.databinding.FragmentTaskDetailsBinding

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   10:07 PM
Project Redcat Scheduler
 */
class TaskDetailFragment : BaseFragment<FragmentTaskDetailsBinding>() {
    override fun getViewBinding(): FragmentTaskDetailsBinding =
        FragmentTaskDetailsBinding.inflate(layoutInflater)
}
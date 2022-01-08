package com.bhuvancom.redcatscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.databinding.FragmentAddEditTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:38 PM
Project Redcat Scheduler
 */
@AndroidEntryPoint
class AddNewTask : BottomSheetDialogFragment() {
    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_add_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditTaskBinding.bind(view)
    }
}
package com.bhuvancom.redcatscheduler.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   09:23 PM
Project Redcat Scheduler
 */
abstract class BaseFragment<VBind : ViewBinding> : Fragment() {
    private var _binding: VBind? = null
    protected val binding get() = _binding!!
    protected abstract fun getViewBinding(): VBind


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    open fun setupView() {}

    private fun init() {
        _binding = getViewBinding()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.bhuvancom.redcatscheduler.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.work.*
import com.bhuvancom.redcatscheduler.R
import com.bhuvancom.redcatscheduler.data.model.Task
import com.bhuvancom.redcatscheduler.databinding.FragmentAddEditTaskBinding
import com.bhuvancom.redcatscheduler.util.TaskSchedule
import com.bhuvancom.redcatscheduler.util.TaskSchedulerCreator
import com.bhuvancom.redcatscheduler.util.Util
import com.bhuvancom.redcatscheduler.util.Util.sdf
import com.bhuvancom.redcatscheduler.util.Util.showToast
import com.bhuvancom.redcatscheduler.viewModel.TaskViewModel
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit

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
    private val args by navArgs<AddNewTaskArgs>()
    private val taskVm by viewModels<TaskViewModel>()
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    var date = Date()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_add_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditTaskBinding.bind(view)

        binding.apply {
            spinnerTaskPriority.apply {
                setAdapter(ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.task_priority))
                )
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long,
                    ) {
                        context?.showToast("Selected ${parent?.selectedItem.toString()}")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }

            tvDateTime.apply {
                text = getString(R.string.execution_time, "Tap to select")
                setOnClickListener {
                    DatePickerFragment() { selectedDate ->
                        text = getString(R.string.execution_time, selectedDate.sdf())
                        date = selectedDate
                        context?.showToast(selectedDate.sdf())
                    }.show(
                        childFragmentManager,
                        "datePicker"
                    )
                }
            }

            spinnerTaskType.apply {
                setAdapter(ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    resources.getStringArray(R.array.task_type)
                ))


            }
        }

        if (args.taskId != -1) {
            binding.tvTitle.text = getString(R.string.edit_task)
            lifecycleScope.launchWhenCreated {
                taskVm.getTask(args.taskId)?.let { task: Task ->
                    binding.apply {
                        etData.editText?.setText(task.data)
                        etTitle.editText?.setText(task.taskName)
                        val idxTT = Util.indexOf(R.array.task_type,
                            task.taskType.name,
                            requireContext())
                        if (idxTT >= 0)
                            spinnerTaskType.setText(idxTT)

                        val idxP = Util.indexOf(R.array.task_priority,
                            task.taskPriority.name,
                            requireContext())

                        if (idxP >= 0)
                            spinnerTaskPriority.setSelection(idxP)
                    }
                }
            }
        }

        binding.btnSave.setOnClickListener {
            saveTask()
        }
    }

    private fun saveTask() {
        if (validateForm()) {
            if (date.after(Date()).not()) {
                context?.showToast("Select correct date and time")
                return
            }
            val taskName = binding.etTitle.editText?.text?.toString() ?: ""
            val data = binding.etData.editText?.text?.toString() ?: ""
            val taskPriority =
                Util.taskPriority(binding.spinnerTaskPriority.text.toString())
            val taskType = Util.taskType(binding.spinnerTaskType.text.toString())
            // todo save the task in db
            if (args.taskId != -1) {
                lifecycleScope.launchWhenCreated {
                    val copy: Task? = taskVm.getTask(args.taskId)?.copy(
                        updatedOn = Date(),
                        taskName = taskName,
                        data = data,
                        taskPriority = taskPriority,
                        taskType = taskType
                    )
                    copy?.let {
                        taskVm.saveTask(it) {

                        }
                        context?.showToast(getString(R.string.task_updated))
                    }
                }
                //re-schedule
                return
            }
            val task = Task(
                taskName = taskName,
                data = data,
                taskTime = date,
                taskPriority = taskPriority,
                taskType = taskType,
                isRepeating = binding.checkRepeat.isChecked
            )
            taskVm.saveTask(task) {
                context?.showToast(getString(R.string.task_added))
                // schedule the job
                val dataInput = Data.Builder()
                    .putInt("taskId", it.toInt())
                    .build()

                val currentTime = System.currentTimeMillis()
                val taskTime = task.taskTime.time
                val delay = taskTime - currentTime
                Log.d(TAG, "saveTask: delay $delay")
                TaskSchedulerCreator(requireContext()).create(task.copy(taskId = it.toInt()))
                /*
                if (task.isRepeating) {
                    val work = PeriodicWorkRequestBuilder<TaskSchedule>(24,
                        TimeUnit.HOURS,
                        delay,
                        TimeUnit.MILLISECONDS
                    )
                        .addTag(it.toString())
                        .setInputData(dataInput)
                        .build()
                    val workM = WorkManager.getInstance(requireContext()).enqueue(work)
                } else {
                    val work = OneTimeWorkRequestBuilder<TaskSchedule>()
                        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                        .addTag(it.toString())
                        .setInputData(dataInput)
                        .build()
                    val workM = WorkManager.getInstance(requireContext()).enqueue(work)
                }

                 */
                dismiss()
            }
        }
    }

    private fun validateForm(): Boolean {
        return FormValidator.getInstance()
            .addField(binding.etTitle, NonEmptyRule(getString(R.string.enter_title)))
            .addField(binding.spinnerTaskPriority,
                NonEmptyRule(getString(R.string.select_task_priority)))
            .addField(binding.spinnerTaskType, NonEmptyRule(getString(R.string.select_task_type)))
            .validate()
    }

    fun inputWorkData(taskId: Int) = workDataOf("taskId" to taskId)

    companion object {
        private const val TAG = "AddNewTask"
    }
}
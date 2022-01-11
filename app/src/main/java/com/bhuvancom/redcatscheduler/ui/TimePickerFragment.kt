package com.bhuvancom.redcatscheduler.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(private val date: Date, private inline val onTimeSelect: (Date) -> Unit) :
    DialogFragment(),
    TimePickerDialog.OnTimeSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        isCancelable = false
        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(activity, this, hour, minute,
            DateFormat.is24HourFormat(activity))
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        val time: Date = Calendar.getInstance().apply {
            time = date
        }.apply {
            this.set(Calendar.HOUR_OF_DAY, hourOfDay)
            this.set(Calendar.MINUTE, minute)
            this.set(Calendar.SECOND, 0)
        }.time
        onTimeSelect.invoke(time)
    }
}
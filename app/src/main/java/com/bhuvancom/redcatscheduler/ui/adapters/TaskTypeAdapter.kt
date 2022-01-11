package com.bhuvancom.redcatscheduler.ui.adapters

import android.content.Context
import android.widget.ArrayAdapter

/**
@author Bhuvaneshvar
Date    09-Jan-2022
Time   12:00 AM
Project Redcat Scheduler
 */
class TaskTypeAdapter(private val contextOf: Context) :
    ArrayAdapter<String>(contextOf, android.R.layout.simple_spinner_dropdown_item) {

}
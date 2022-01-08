package com.bhuvancom.redcatscheduler.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhuvancom.redcatscheduler.databinding.LoadStateBinding

/**
@author Bhuvaneshvar
Date    08-Jan-2022
Time   11:00 PM
Project Redcat Scheduler
 */
class LoadStateAdapter(private inline val retry: () -> Unit) :
    LoadStateAdapter<com.bhuvancom.redcatscheduler.ui.adapters.LoadStateAdapter.LoadStateVH>() {
    inner class LoadStateVH(val binding: LoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onBindViewHolder(holder: LoadStateVH, loadState: LoadState) {
        holder.binding.apply {
            progressBar.isVisible = loadState is LoadState.Loading
            btnRetry.isVisible = loadState !is LoadState.Loading
            tvError.isVisible = loadState !is LoadState.Loading

            root.setOnClickListener {
                retry.invoke()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateVH {
        return LoadStateVH(
            LoadStateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
}
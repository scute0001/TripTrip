package com.emil.triptrip.ui.tripdetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.R
import com.emil.triptrip.database.DayKey
import com.emil.triptrip.databinding.ListTripDaysBinding
import com.google.api.ResourceProto.resource
import okhttp3.internal.notify

class SelectDayAdapter(val viewModel: TripDetailViewModel): ListAdapter<DayKey, DayViewHolder>(DayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        return DayViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

}


class DayViewHolder(val binding: ListTripDaysBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: TripDetailViewModel, item: DayKey) {
        binding.dayKey = item
        binding.viewModel = viewModel

        binding.root.setOnClickListener {
            //record selected position
            viewModel.selectedDayPosition = adapterPosition
            viewModel.selectedDay.value = item.dayCount
            viewModel.selectedDayKey.value = item
            viewModel.refreshSelectedDayAdapter.value = true
        }

        if (adapterPosition == viewModel.selectedDayPosition) {
            binding.constraintDay.setBackgroundResource(R.drawable.shape_selected_card)

        } else {
            binding.constraintDay.setBackgroundResource(R.drawable.shape_trip_day_card)
        }

        binding.executePendingBindings()
    }
    companion object {
        fun from(parent: ViewGroup): DayViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListTripDaysBinding.inflate(inflater, parent, false)
            return DayViewHolder(binding)
        }
    }

}


class DayDiffCallback: DiffUtil.ItemCallback<DayKey>() {
    override fun areItemsTheSame(oldItem: DayKey, newItem: DayKey): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: DayKey, newItem: DayKey): Boolean {
        return oldItem == newItem
    }
}
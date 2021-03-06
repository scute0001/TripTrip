package com.emil.triptrip.ui.mytrips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.Trip
import com.emil.triptrip.databinding.ListTripsBinding


class TripsAdapter(val viewModel: MyTripsViewModel): ListAdapter<Trip, TripViewHolder>(
    DiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        return TripViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

class TripViewHolder(val binding: ListTripsBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: MyTripsViewModel, item: Trip) {
        binding.tripData = item

        // set users to recyclerview
        val adapter = UsersAdapter()
        binding.recyclerListAttendUsers.adapter = adapter
        adapter.submitList(item.attendUserList)

        // set data and click function to navigation to trip detail page
        binding.root.setOnClickListener {
            viewModel._navToTripDetail.value = item
        }
    }

    companion object {
        fun from(parent: ViewGroup): TripViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListTripsBinding.inflate(inflater, parent, false)
            return TripViewHolder(binding)
        }
    }
}

class DiffCallback: DiffUtil.ItemCallback<Trip>() {
    override fun areItemsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Trip, newItem: Trip): Boolean {
        return oldItem == newItem
    }
}
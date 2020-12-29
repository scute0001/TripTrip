package com.emil.triptrip.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.NotificationAddTrip
import com.emil.triptrip.databinding.ListNotificationBinding



class NotificationAdapter(val viewModel: NotificationViewModel): ListAdapter<NotificationAddTrip, NotificationViewHolder>(
    NotificationDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

class NotificationViewHolder(val binding: ListNotificationBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: NotificationViewModel, item: NotificationAddTrip) {
        binding.data = item

        // set data and click function to navigation to trip detail page
        binding.root.setOnClickListener {
            if (item.tripId != null) {
                viewModel.getTripData(item.tripId)
            }
        }

    }
    companion object {
        fun from(parent: ViewGroup): NotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListNotificationBinding.inflate(inflater, parent, false)
            return NotificationViewHolder(binding)
        }
    }

}


class NotificationDiffCallback: DiffUtil.ItemCallback<NotificationAddTrip>() {
    override fun areItemsTheSame(oldItem: NotificationAddTrip, newItem: NotificationAddTrip): Boolean {
        return oldItem.tripId == newItem.tripId
    }

    override fun areContentsTheSame(oldItem: NotificationAddTrip, newItem: NotificationAddTrip): Boolean {
        return oldItem == newItem
    }
}
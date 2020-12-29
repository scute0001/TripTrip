package com.emil.triptrip.ui.mytrips

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.AttendUser
import com.emil.triptrip.databinding.ListAttendUserBinding


class UsersAdapter(): ListAdapter<AttendUser, UsersViewHolder>(
    UsersDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class UsersViewHolder(val binding: ListAttendUserBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: AttendUser) {
        binding.user = item

    }
    companion object {
        fun from(parent: ViewGroup): UsersViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListAttendUserBinding.inflate(inflater, parent, false)
            return UsersViewHolder(binding)
        }
    }

}


class UsersDiffCallback: DiffUtil.ItemCallback<AttendUser>() {
    override fun areItemsTheSame(oldItem: AttendUser, newItem: AttendUser): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: AttendUser, newItem: AttendUser): Boolean {
        return oldItem == newItem
    }
}
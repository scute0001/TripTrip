package com.emil.triptrip.ui.addtrip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.User
import com.emil.triptrip.databinding.ListAddTripUsersBinding


class AttendUsersAdapter(): ListAdapter<User, AddUsersViewHolder>(
    UsersDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUsersViewHolder {
        return AddUsersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddUsersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AddUsersViewHolder(val binding: ListAddTripUsersBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(item: User) {
        binding.users = item
    }
    companion object {
        fun from(parent: ViewGroup): AddUsersViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListAddTripUsersBinding.inflate(inflater, parent, false)
            return AddUsersViewHolder(binding)
        }
    }
}

class UsersDiffCallback: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.email== newItem.email
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
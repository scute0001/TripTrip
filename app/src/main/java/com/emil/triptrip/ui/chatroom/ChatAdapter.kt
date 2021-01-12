package com.emil.triptrip.ui.chatroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.Message
import com.emil.triptrip.databinding.ListMessageBinding
import com.emil.triptrip.ui.login.UserManager


class ChatAdapter(val viewModel: ChatRoomViewModel): ListAdapter<Message, MessageViewHolder>(
    MessageDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

class MessageViewHolder(val binding: ListMessageBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: ChatRoomViewModel, item: Message) {
        binding.message = item
        binding.viewModel = viewModel

        if (item.name == UserManager.user.value?.name) {
            binding.constraintMyMessage.visibility = View.VISIBLE
            binding.constraintFriendMessage.visibility = View.GONE
        } else {
            binding.constraintMyMessage.visibility = View.GONE
            binding.constraintFriendMessage.visibility = View.VISIBLE
        }
    }

    companion object {
        fun from(parent: ViewGroup): MessageViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListMessageBinding.inflate(inflater, parent, false)
            return MessageViewHolder(binding)
        }
    }
}

class MessageDiffCallback: DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
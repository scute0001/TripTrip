package com.emil.triptrip.ui.tripdetail


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.databinding.ListTripDetailImgsBinding


class SpotPicAdapter(val viewModel: TripDetailViewModel): ListAdapter<String, PicViewHolder>(SpotPicDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
        return PicViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}


class PicViewHolder(val binding: ListTripDetailImgsBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: TripDetailViewModel, item: String) {
        binding.pic = item
    }

    companion object {
        fun from(parent: ViewGroup): PicViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListTripDetailImgsBinding.inflate(inflater, parent, false)
            return PicViewHolder(binding)
        }
    }
}


class SpotPicDiffCallback: DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }
}
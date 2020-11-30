package com.emil.triptrip.ui.tripdetail

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.emil.triptrip.database.SpotTag
import com.emil.triptrip.databinding.ListOneDayTimeBinding


class SelectTimeAdapter(val viewModel: TripDetailViewModel): ListAdapter<SpotTag, SpotViewHolder>(SpotDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpotViewHolder {
        return SpotViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SpotViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }

}


class SpotViewHolder(val binding: ListOneDayTimeBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: TripDetailViewModel, item: SpotTag) {
        Log.i("item", "item is $item")
        binding.spotData = item

    }
    companion object {
        fun from(parent: ViewGroup): SpotViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListOneDayTimeBinding.inflate(inflater, parent, false)
            return SpotViewHolder(binding)
        }
    }

}


class SpotDiffCallback: DiffUtil.ItemCallback<SpotTag>() {
    override fun areItemsTheSame(oldItem: SpotTag, newItem: SpotTag): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SpotTag, newItem: SpotTag): Boolean {
        return oldItem == newItem
    }
}
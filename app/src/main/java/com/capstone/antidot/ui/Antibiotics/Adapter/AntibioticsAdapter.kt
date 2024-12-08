package com.capstone.antidot.ui.Antibiotics.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.antidot.api.models.AntibioticsItem
import com.capstone.antidot.databinding.ItemAntibioticsBinding

class EventAdapter(private val onItemClick: (AntibioticsItem) -> Unit) :
    ListAdapter<AntibioticsItem, EventAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAntibioticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    class MyViewHolder(val binding: ItemAntibioticsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: AntibioticsItem) {
            binding.tvItemName.text = event.antibioticsName
            binding.tvItemDescription.text = event.antibioticsUsage

            Glide.with(binding.imgItemPhoto.context)
                .load(event.antibioticImage)
                .into(binding.imgItemPhoto)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AntibioticsItem>() {
            override fun areItemsTheSame(
                oldItem: AntibioticsItem,
                newItem: AntibioticsItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: AntibioticsItem,
                newItem: AntibioticsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
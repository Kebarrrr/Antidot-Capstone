package com.capstone.antidot.ui.Reminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.antidot.api.models.AntibioticsItem
import com.capstone.antidot.databinding.ItemAntibioticsBinding

class AddReminderAdapter(private val onItemClick: (AntibioticsItem) -> Unit) :
    RecyclerView.Adapter<AddReminderAdapter.MyViewHolder>() {

    private val originalList = mutableListOf<AntibioticsItem>()
    private val filteredList = mutableListOf<AntibioticsItem>()

    fun submitList(events: List<AntibioticsItem>) {
        originalList.clear()
        originalList.addAll(events)
        filteredList.clear()
        filteredList.addAll(events)
        notifyDataSetChanged()
    }

    fun filter(query: String?) {
        filteredList.clear()
        if (query.isNullOrEmpty()) {
            filteredList.addAll(originalList)
        } else {
            filteredList.addAll(originalList.filter {
                it.antibioticsName.contains(query, ignoreCase = true)
            })
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemAntibioticsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = filteredList[position]
        holder.bind(event)

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }
    }

    override fun getItemCount(): Int = filteredList.size

    class MyViewHolder(private val binding: ItemAntibioticsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: AntibioticsItem) {
            binding.tvItemName.text = event.antibioticsName
            binding.tvItemDescription.text = event.antibioticsUsage

            Glide.with(binding.imgItemPhoto.context)
                .load(event.antibioticImage)
                .into(binding.imgItemPhoto)
        }
    }


}

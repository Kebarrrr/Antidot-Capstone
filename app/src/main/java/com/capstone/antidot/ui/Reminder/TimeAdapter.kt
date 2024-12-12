package com.capstone.antidot.ui.Reminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R

class TimeAdapter(private var timeList: List<String>, private val onTimeChanged: (String) -> Unit) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    inner class TimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val btnChangeTime: Button = itemView.findViewById(R.id.btn_change_time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_jam, parent, false)
        return TimeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val time = timeList[position]
        holder.tvTime.text = time

        // Set listener untuk tombol perubahan waktu
        holder.btnChangeTime.setOnClickListener {
            // Panggil onTimeChanged callback saat tombol diklik
            onTimeChanged(time)
        }
    }

    override fun getItemCount(): Int {
        return timeList.size
    }
    fun updateData(newDoses: List<String>) {
        timeList = newDoses
        notifyDataSetChanged()  // Memberitahu adapter bahwa data telah berubah
    }
}

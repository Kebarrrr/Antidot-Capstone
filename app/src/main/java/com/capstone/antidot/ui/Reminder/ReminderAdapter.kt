package com.capstone.antidot.ui.Reminder

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.AlarmReceiver
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.RemindersItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class ReminderAdapter(private var reminderList: List<RemindersItem>, private val context: Context) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemJam: TextView = itemView.findViewById(R.id.tvItemJam)
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemName)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_reminder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminderItem = reminderList[position]

        holder.tvItemName.text = reminderItem.customAntibioticName
        val reminderTimes = reminderItem.reminderTimes.joinToString("\n")
        holder.tvItemJam.text = reminderTimes

        holder.btnDelete.setOnClickListener {
            val alertDialog = android.app.AlertDialog.Builder(context)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Apakah Anda yakin ingin menghapus pengingat ini?")
                .setPositiveButton("Ya") { dialog, which ->
                    deleteReminder(reminderItem.reminderID.toString())
                }
                .setNegativeButton("Tidak") { dialog, which ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return reminderList.size
    }

    fun updateList(newReminderList: List<RemindersItem>) {
        val diffCallback = ReminderDiffCallback(reminderList, newReminderList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        reminderList = newReminderList
        diffResult.dispatchUpdatesTo(this)
    }

    private fun deleteReminder(reminderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = RetrofitClient.getInstance(context)
                val response: Response<Void> = apiService.deleteReminder(reminderId)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val indexToRemove = reminderList.indexOfFirst { it.reminderID.toString() == reminderId }
                        if (indexToRemove != -1) {
                            val removedItem = reminderList[indexToRemove]
                            reminderList = reminderList.toMutableList().apply { removeAt(indexToRemove) }
                            notifyItemRemoved(indexToRemove)

                            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                            val notificationId = reminderId.hashCode()
                            notificationManager.cancel(notificationId)

                            // Membatalkan alarm
                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val intent = Intent(context, AlarmReceiver::class.java).apply {
                                putExtra("REMINDER_ID", reminderId.hashCode())
                            }
                            val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                reminderId.hashCode(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )

                            alarmManager.cancel(pendingIntent)

                            Toast.makeText(context, "Reminder deleted successfully", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("ReminderAdapter", "Failed to delete reminder: ${response.code()}")
                        Toast.makeText(context, "Failed to delete reminder", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ReminderAdapter", "Error deleting reminder", e)
                    Toast.makeText(context, "Error deleting reminder", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

class ReminderDiffCallback(
    private val oldList: List<RemindersItem>,
    private val newList: List<RemindersItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].reminderID == newList[newItemPosition].reminderID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

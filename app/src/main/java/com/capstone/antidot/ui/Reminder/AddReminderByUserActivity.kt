package com.capstone.antidot.ui.Reminder

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.AlarmReceiver
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.ReminderRequestByID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddReminderByUserActivity : AppCompatActivity() {

    private lateinit var rvJam: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSaveReminder: Button
    private var doses: List<String> = listOf("06:00", "15:00", "22:00") // Dosis default
    private lateinit var timeAdapter: TimeAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                setAlarmsForTimes(doses)
            } else {
                Toast.makeText(
                    this,
                    "Izin alarm tidak diberikan. Harap aktifkan izin di pengaturan.",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder_by_user)

        val namaObat = intent.getStringExtra("NAMA_OBAT")

        rvJam = findViewById(R.id.rv_jam)
        progressBar = findViewById(R.id.progressBar)
        btnSaveReminder = findViewById(R.id.btn_save_reminder_user)

        val obatTextView: TextView = findViewById(R.id.obat)
        obatTextView.text = namaObat ?: "Nama Obat Tidak Ditemukan"

        val dosisOptions = arrayOf("1x sehari", "2x sehari", "3x sehari")
        val dosisAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dosisOptions)

        val dosisAutoCompleteTextView: AutoCompleteTextView = findViewById(R.id.dosis)
        dosisAutoCompleteTextView.setAdapter(dosisAdapter)
        dosisAutoCompleteTextView.setThreshold(1)

        dosisAutoCompleteTextView.addTextChangedListener {
            val selectedDosis = it.toString().trim()
            updateDosesBasedOnSelection(selectedDosis)
        }

        dosisAutoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val selectedDosis = dosisAutoCompleteTextView.text.toString().trim()
                Log.d("ReminderData", "Dosis setelah kehilangan fokus: '$selectedDosis'")
            }
        }

        setupRecyclerView()

        btnSaveReminder.setOnClickListener {
            val selectedDosis = dosisAutoCompleteTextView.text.toString().trim()
            if (selectedDosis.isEmpty() || doses.isEmpty()) {
                Toast.makeText(this, "Pilih dosis terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkExactAlarmPermission()

            val obatTextView = findViewById<TextView>(R.id.obat).text.toString()
            postReminderToAPI(obatTextView, selectedDosis)
        }
    }

    private fun updateDosesBasedOnSelection(selectedDosis: String) {
        doses = when (selectedDosis) {
            "1x sehari" -> listOf("06:00")
            "2x sehari" -> listOf("06:00", "15:00")
            "3x sehari" -> listOf("06:00", "15:00", "22:00")
            else -> listOf()
        }

        if (::timeAdapter.isInitialized) {
            timeAdapter.updateData(doses)
        } else {
            Log.e("AddReminderByUserActivity", "TimeAdapter belum diinisialisasi.")
        }
    }

    private fun setupRecyclerView() {
        rvJam.layoutManager = LinearLayoutManager(this)
        timeAdapter = TimeAdapter(doses) { time ->
            showTimePickerDialog(time)
        }
        rvJam.adapter = timeAdapter
    }

    private fun showTimePickerDialog(currentTime: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                updateTimeInRecyclerView(currentTime, formattedTime)
            },
            hour, minute, true
        )
        timePickerDialog.show()
    }

    private fun updateTimeInRecyclerView(oldTime: String, newTime: String) {
        doses = doses.map { if (it == oldTime) newTime else it }
        timeAdapter.updateData(doses)
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM)
            } else {
                setAlarmsForTimes(doses)
            }
        } else {
            setAlarmsForTimes(doses)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setAlarmsForTimes(times: List<String>) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()

        for (time in times) {
            val timeParts = time.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.timeInMillis < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            val antibioticName = findViewById<TextView>(R.id.obat).text.toString()
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("TIME", time)
                putExtra("MEDICATION_NAME", antibioticName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                times.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun postReminderToAPI(customAntibioticName: String, dosageFrequency: String) {
        val reminderRequest = ReminderRequestByID(
            reminderFrequency = dosageFrequency,
            reminderTimes = doses,
            customAntibioticName = customAntibioticName
        )

        val apiService = RetrofitClient.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.postReminderByID(reminderRequest)
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        Toast.makeText(
                            this@AddReminderByUserActivity,
                            response.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddReminderByUserActivity,
                            "Gagal menyimpan pengingat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddReminderByUserActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

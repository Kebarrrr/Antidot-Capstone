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
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.AlarmReceiver
import com.capstone.antidot.R
import java.util.Calendar
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.ReminderRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddReminderByDatabaseActivity : AppCompatActivity() {

    private lateinit var rvJam: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSaveReminder: Button
    private var doses: List<String> = listOf("06:00", "15:00", "22:00") // Dosis default
    private lateinit var addreminderbydataViewModel: AddReminderByDatabaseViewModel
    private lateinit var timeAdapter: TimeAdapter

    // Launcher untuk permintaan izin
    @RequiresApi(Build.VERSION_CODES.S)
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Jika izin diberikan, lanjutkan untuk mengatur alarm
            setAlarmsForTimes(doses)
        } else {
            // Jika izin ditolak, beri tahu pengguna
            Toast.makeText(this, "Izin alarm tidak diberikan. Harap aktifkan izin di pengaturan.", Toast.LENGTH_SHORT).show()
            // Arahkan ke pengaturan izin
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder_by_database)

        // Ambil antibioticID dari Intent
        val antibioticID = intent.getStringExtra("EVENT_ID")

        // Inisialisasi UI komponen
        rvJam = findViewById(R.id.rv_jam)
        progressBar = findViewById(R.id.progressBar)
        btnSaveReminder = findViewById(R.id.btn_save_reminder)

        // Setup ViewModel
        addreminderbydataViewModel = ViewModelProvider(
            this,
            AddReminderByDatabaseViewModelFactory(application)
        )[AddReminderByDatabaseViewModel::class.java]

        // Ambil data antibiotik berdasarkan antibioticID
        addreminderbydataViewModel.getAntibioticById(antibioticID.toString())

        // Observe data yang diterima dari ViewModel
        addreminderbydataViewModel.antibiotic.observe(this) { antibiotic ->
            findViewById<TextView>(R.id.obat).text = antibiotic.antibioticsName
            findViewById<TextView>(R.id.dosis).text = antibiotic.antibioticFrequencyUsagePerDay

            // Tentukan dosis berdasarkan frekuensi
            val dosageFrequency = antibiotic.antibioticFrequencyUsagePerDay

            // Atur dosis default berdasarkan frekuensi
            doses = when (dosageFrequency) {
                "1x sehari" -> listOf("06:00") // 1x sehari
                "2x sehari" -> listOf("06:00", "15:00") // 2x sehari
                "3x sehari" -> listOf("06:00", "15:00", "22:00") // 3x sehari
                else -> listOf("06:00") // Default jika tidak sesuai
            }

            // Setup RecyclerView untuk menampilkan waktu dosis
            setupRecyclerView()

            // Menyimpan reminder dan mengatur alarm
            btnSaveReminder.setOnClickListener {
                // Meminta izin untuk alarm sebelum menyimpan
                checkExactAlarmPermission()

                // Kirim reminder ke API
                postReminderToAPI(antibiotic.antibioticID, dosageFrequency)
            }
        }

        // Setup RecyclerView
        setupRecyclerView()
    }

    // Setup RecyclerView untuk menampilkan dosis
    private fun setupRecyclerView() {
        rvJam.layoutManager = LinearLayoutManager(this)
        timeAdapter = TimeAdapter(doses) { time ->
            showTimePickerDialog(time)
        }
        rvJam.adapter = timeAdapter
    }

    // Dialog untuk memilih waktu pengingat
    private fun showTimePickerDialog(currentTime: String) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Menampilkan dialog pemilih waktu
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

    // Update waktu pada RecyclerView
    private fun updateTimeInRecyclerView(oldTime: String, newTime: String) {
        // Update data dosis
        doses = doses.map { if (it == oldTime) newTime else it }

        // Perbarui data pada adapter tanpa membuat instance baru
        timeAdapter.updateData(doses)
    }

    // Cek izin untuk Android 12 ke atas
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

    // Set alarm sesuai waktu yang dipilih
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

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }

    // Mengirim data reminder ke API
    private fun postReminderToAPI(antibioticID: Int, dosageFrequency: String) {
        val reminderRequest = ReminderRequest(
            reminderFrequency = dosageFrequency,
            reminderTimes = doses,
            antibioticID = antibioticID
        )

        val apiService = RetrofitClient.getInstance(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.postReminder(reminderRequest)
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        Toast.makeText(this@AddReminderByDatabaseActivity, response.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddReminderByDatabaseActivity, "Gagal menyimpan pengingat", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddReminderByDatabaseActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

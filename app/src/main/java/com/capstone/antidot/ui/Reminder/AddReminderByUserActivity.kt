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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.AlarmReceiver
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.ReminderRequest
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
        setContentView(R.layout.activity_add_reminder_by_user)

        val namaObat = intent.getStringExtra("NAMA_OBAT")

        rvJam = findViewById(R.id.rv_jam)
        progressBar = findViewById(R.id.progressBar)
        btnSaveReminder = findViewById(R.id.btn_save_reminder)

        val obatTextView: TextView = findViewById(R.id.obat)
        obatTextView.text = namaObat ?: "Nama Obat Tidak Ditemukan"

        val dosisOptions = arrayOf("1x sehari", "2x sehari", "3x sehari")  // Pilihan dosis
        Log.d("DosisOptions", dosisOptions.joinToString(", "))  // Pastikan data ada

        val dosisAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dosisOptions)

        // Menyambungkan adapter ke AutoCompleteTextView
        val dosisAutoCompleteTextView: AutoCompleteTextView = findViewById(R.id.dosis)
        dosisAutoCompleteTextView.setAdapter(dosisAdapter)
        dosisAutoCompleteTextView.setThreshold(1)  // Tampilkan dropdown setelah 1 karakter

        // Setup listener untuk menangani perubahan pada AutoCompleteTextView
        dosisAutoCompleteTextView.addTextChangedListener {
            val selectedDosis = it.toString()
            updateDosesBasedOnSelection(selectedDosis)
        }

        // Setup RecyclerView dan timeAdapter
        setupRecyclerView()

        // Menampilkan dosis pertama kali berdasarkan default
        val selectedDosis = dosisAutoCompleteTextView.text.toString()
        updateDosesBasedOnSelection(selectedDosis)
    }

    private var hasShownToast = false

    // Fungsi untuk memperbarui list dosis berdasarkan pilihan
    private fun updateDosesBasedOnSelection(selectedDosis: String) {
        doses = when (selectedDosis) {
            "1x sehari" -> listOf("06:00")  // 1x sehari
            "2x sehari" -> listOf("06:00", "15:00")  // 2x sehari
            "3x sehari" -> listOf("06:00", "15:00", "22:00")  // 3x sehari
            else -> listOf()  // Default jika tidak sesuai
        }

        if (doses.isEmpty() && !hasShownToast) {
            Toast.makeText(this, "Tidak ada dosis yang tersedia, Masukan 1x, 2x atau 3x sehari", Toast.LENGTH_SHORT).show()
            hasShownToast = true  // Tandai bahwa Toast sudah ditampilkan
        }

        // Jika dosis valid, reset flag untuk Toast
        if (doses.isNotEmpty()) {
            hasShownToast = false
        }

        // Pastikan timeAdapter sudah diinisialisasi sebelum dipanggil
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
                        Toast.makeText(this@AddReminderByUserActivity, response.message, Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@AddReminderByUserActivity, "Gagal menyimpan pengingat", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddReminderByUserActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

package com.capstone.antidot.ui.Reminder

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R
import com.capstone.antidot.ui.Antibiotics.ViewModelFactory

class AddReminderByDatabaseActivity : AppCompatActivity() {

    private lateinit var rvJam: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnSaveReminder: Button
    private var doses: List<String> = listOf("06:00", "15:00", "22:00") // Set default doses
    private lateinit var addreminderbydataViewModel: AddReminderByDatabaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder_by_database)

        // Ambil antibioticID dari Intent
        val antibioticID = intent.getStringExtra("EVENT_ID")

        // Inisialisasi RecyclerView dan ProgressBar
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

        // Observe data yang diambil dari ViewModel
        addreminderbydataViewModel.antibiotic.observe(this) { antibiotic ->
            // Update UI dengan data yang diterima
            findViewById<TextView>(R.id.obat).text = antibiotic.antibioticsName
            findViewById<TextView>(R.id.dosis).text = antibiotic.antibioticFrequencyUsagePerDay

            // Tentukan jumlah dosis per hari dan sesuaikan waktu pengingat
            val dosageFrequency = antibiotic.antibioticFrequencyUsagePerDay

            // Atur dosis default berdasarkan frekuensi
            doses = when (dosageFrequency) {
                "1x sehari" -> listOf("06:00") // 1x sehari
                "2x sehari" -> listOf("06:00", "15:00") // 2x sehari
                "3x sehari" -> listOf("06:00", "15:00", "22:00") // 3x sehari
                else -> listOf("06:00") // Default jika tidak sesuai
            }

            // Simpan dosis yang diterima untuk RecyclerView
          /*  doses = antibiotic.reminderTimes*/ // Misalnya, waktu pengingat (jam) berdasarkan dosis
            setupRecyclerView()
        }


        // Atur RecyclerView dengan data dosis
        setupRecyclerView()

        // Misalnya, Anda dapat menambahkan fungsi lain untuk menyimpan reminder saat tombol save diklik
        btnSaveReminder.setOnClickListener {
            // Lakukan penyimpanan reminder
        }
    }

    // Setup RecyclerView untuk menampilkan data dosis
    private fun setupRecyclerView() {
        rvJam.layoutManager = LinearLayoutManager(this)
        val adapter = TimeAdapter(doses)
        rvJam.adapter = adapter
    }
}

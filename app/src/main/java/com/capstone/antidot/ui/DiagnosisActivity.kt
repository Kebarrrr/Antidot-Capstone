package com.capstone.antidot.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.Symptom
import com.capstone.antidot.api.models.SymptomsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DiagnosisActivity : AppCompatActivity() {

    private lateinit var adapter: DiagnosisAdapter
    private lateinit var symptoms: MutableList<Symptom>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosis)

        // Data awal (symptom bawaan)
        symptoms = mutableListOf(
            Symptom(arrayOf("Gejala 1")),
            Symptom(arrayOf("Gejala 2")),
            Symptom(arrayOf("Gejala 3"))
        )

        // Inisialisasi RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        adapter = DiagnosisAdapter(symptoms) { position ->
            adapter.removeSymptom(position)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Tambahkan symptom baru
        val btnAddSymptom: Button = findViewById(R.id.btn_add_symptom)
        btnAddSymptom.setOnClickListener {
            val newSymptom = Symptom(arrayOf("Gejala ${symptoms.size + 1}"), true)
            symptoms.add(newSymptom)
            adapter.notifyItemInserted(symptoms.size - 1)
        }

        // Tombol untuk menganalisis gejala
        val btnAnalyze: Button = findViewById(R.id.btn_analyze)
        btnAnalyze.setOnClickListener {
            analyzeSymptoms()
        }
    }

    private fun analyzeSymptoms() {
        // Ambil gejala yang sudah dipilih
        val initialSymptoms = symptoms.flatMap { it.symptoms.toList() } // Semua gejala (awal)
        val selectedSymptoms = symptoms.filter { it.isUserAdded }.flatMap { it.symptoms.toList() } // Gejala yang dipilih

        // Periksa apakah ada gejala yang dipilih
        if (selectedSymptoms.isEmpty() && initialSymptoms.isEmpty()) {
            Toast.makeText(this, "Pilih gejala terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }

        // Kirimkan data ke API untuk mendapatkan diagnosis
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Membuat SymptomsRequest yang menggabungkan selectedSymptoms dan initialSymptoms
                val symptomsRequest = SymptomsRequest(
                    selectedSymptoms.toTypedArray(), // Mengubah menjadi Array
                    initialSymptoms.toTypedArray()   // Mengubah menjadi Array
                )

                // Ambil Retrofit instance dan lakukan API call
                val response = RetrofitClient.getInstance(this@DiagnosisActivity).diagnosis(symptomsRequest)

                // Periksa apakah response berhasil
                if (response != null) {
                    // Kirim hasil ke ResultDiagnosisActivity
                    val intent = Intent(this@DiagnosisActivity, ResultDiagnosisActivity::class.java)
                    intent.putExtra("diagnosisData", response) // Pastikan response bisa diterima di Activity tujuan
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DiagnosisActivity, "Gagal mendapatkan hasil diagnosis", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                val statusCode = e.code() // Kode status HTTP (misalnya 400, 500)
                val errorMessage = e.message() // Pesan error dari server
                Log.e("DiagnosisActivity", "HTTP Error: $statusCode, Message: $errorMessage")
                Toast.makeText(this@DiagnosisActivity, "Kesalahan pada server", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@DiagnosisActivity, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

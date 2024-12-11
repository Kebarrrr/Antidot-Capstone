package com.capstone.antidot.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.antidot.R
import com.capstone.antidot.api.models.PredictResponse
import com.capstone.antidot.databinding.ActivityResultDiagnosisBinding

class ResultDiagnosisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultDiagnosisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_diagnosis)

        // Ambil data diagnosis dari Intent
        val predictResponse: PredictResponse? = intent.getParcelableExtra("diagnosisData")

        // Periksa apakah data tidak null
        if (predictResponse != null) {
            // Update UI dengan hasil diagnosis
            findViewById<TextView>(R.id.penyakit).text = predictResponse.diagnosis.disease
            findViewById<TextView>(R.id.rekomendasi_obat).text = predictResponse.diagnosis.antibioticName
            findViewById<TextView>(R.id.dosis).text = predictResponse.diagnosis.antibioticFrequencyUsagePerDay
            findViewById<TextView>(R.id.deskripsi).text = predictResponse.diagnosis.diseaseDescription
        } else {
            Toast.makeText(this, "Gagal memuat hasil diagnosis", Toast.LENGTH_SHORT).show()
        }

        // Tombol Kembali
        findViewById<Button>(R.id.btn_reminder).setOnClickListener {
            onBackPressed()
        }
    }
}

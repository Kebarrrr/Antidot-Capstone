package com.capstone.antidot.ui

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.AntibioticFrequency
import com.capstone.antidot.api.models.Diagnosis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ResultDiagnosisActivity : AppCompatActivity() {

    private var diagnosis: Diagnosis? = null // Ensure this is at the class level

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_diagnosis)

        // Retrieve the diagnosisID passed from the previous activity
        val diagnosisID = intent.getIntExtra("diagnosisID", -1) // Default value is -1 if no ID is passed

        if (diagnosisID != -1) {
            // Fetch the diagnosis data from the API using the diagnosisID
            fetchDiagnosisData(diagnosisID)
        } else {
            Toast.makeText(this, "Invalid diagnosis ID", Toast.LENGTH_SHORT).show()
        }

        val btnCreateReminder: Button = findViewById(R.id.btn_reminder)
        btnCreateReminder.setOnClickListener { createReminder() }
    }

    private fun fetchDiagnosisData(diagnosisID: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                // Fetch the diagnosis data from the API
                val response = RetrofitClient.getInstance(this@ResultDiagnosisActivity).getPredict(diagnosisID.toString())

                if (response != null) {

                    diagnosis = response.diagnosis

                    diagnosis?.let {
                        updateUI(it)
                    } ?: run {
                        Toast.makeText(this@ResultDiagnosisActivity, "Diagnosis data is empty", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ResultDiagnosisActivity, "No diagnosis data found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Log.e("ResultDiagnosisActivity", "Error fetching diagnosis data: ${e.message()}")
                Toast.makeText(this@ResultDiagnosisActivity, "Error fetching diagnosis data", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@ResultDiagnosisActivity, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(diagnosis: Diagnosis) {
        Log.d("ResultDiagnosisActivity", "Diagnosis: $diagnosis")

        // Set the diagnosis details to the views
        findViewById<TextView>(R.id.penyakit).text = diagnosis.disease
        findViewById<TextView>(R.id.rekomendasi_obat).text = diagnosis.antibioticName
        findViewById<TextView>(R.id.dosis).text = diagnosis.antibioticsDosage
        findViewById<TextView>(R.id.deskripsi).text = diagnosis.diseaseDescription
    }

    private fun createReminder() {
        // Code to create a reminder for the user
    }
}

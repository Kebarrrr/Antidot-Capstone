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
import com.capstone.antidot.api.models.Symptoms
import com.capstone.antidot.api.models.SymptomsRequest
import com.capstone.antidot.api.models.SymptomsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class DiagnosisActivity : AppCompatActivity() {

    private lateinit var adapter: DiagnosisAdapter
    private lateinit var symptoms: MutableList<Symptoms>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosis)

        symptoms = mutableListOf(Symptoms(symptomName = ""), Symptoms(symptomName = ""), Symptoms(symptomName = ""))

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        adapter = DiagnosisAdapter(symptoms) { position ->
            symptoms.removeAt(position)
            adapter.notifyItemRemoved(position)
            if (symptoms.size < 3) {
                symptoms.add(Symptoms(symptomName = ""))
                adapter.notifyItemInserted(symptoms.size - 1)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchSymptoms()

        val btnAnalyze: Button = findViewById(R.id.btn_analyze)
        btnAnalyze.setOnClickListener { analyzeSymptoms() }

        val btnAddSymptom: Button = findViewById(R.id.btn_add_symptom)
        btnAddSymptom.setOnClickListener { addSymptom() }
    }

    private fun fetchSymptoms() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.getInstance(this@DiagnosisActivity).getSymptoms()
                if (response.status == "success") {
                    symptoms.addAll(response.symptoms.map { Symptoms(it.symptomName) })
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@DiagnosisActivity, "Failed to load symptoms", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DiagnosisActivity, "Error fetching symptoms", Toast.LENGTH_SHORT).show()
                Log.e("DiagnosisActivity", "Error fetching symptoms: ${e.message}")
            }
        }
    }

    private fun addSymptom() {
        if (symptoms.size < 3) {
            symptoms.add(Symptoms(symptomName = ""))
            adapter.notifyItemInserted(symptoms.size - 1)
        }
    }

    private fun analyzeSymptoms() {
        val selectedSymptomsNames = adapter.getUserInputs()
        val nonEmptySymptoms = selectedSymptomsNames.filter { it.isNotEmpty() }

        if (nonEmptySymptoms.isNotEmpty()) {
            sendSymptomsToApi(nonEmptySymptoms)
        } else {
            Toast.makeText(this@DiagnosisActivity, "Please select symptoms", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSymptomsToApi(symptoms: List<String>) {
        val symptomsRequest = SymptomsRequest(symptoms)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = RetrofitClient.getInstance(this@DiagnosisActivity).sendSymptoms(symptomsRequest)
                Log.d("DiagnosisActivity", "Response: $response")

                if (response.status == "success") {
                    Toast.makeText(this@DiagnosisActivity, "Symptoms sent successfully", Toast.LENGTH_SHORT).show()
                    val diagnosisID = response.diagnosis.diagnosisID // Extract diagnosisID here

                    // Passing diagnosisID to ResultDiagnosisActivity
                    val intent = Intent(this@DiagnosisActivity, ResultDiagnosisActivity::class.java)
                    intent.putExtra("diagnosisID", diagnosisID) // Send the diagnosisID to the next activity
                    startActivity(intent)
                } else {
                    Toast.makeText(this@DiagnosisActivity, "Failed to analyze symptoms", Toast.LENGTH_SHORT).show()
                }
            } catch (e: HttpException) {
                Log.e("DiagnosisActivity", "Error sending symptoms: ${e.message()}")
                Toast.makeText(this@DiagnosisActivity, "Error connecting to server", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this@DiagnosisActivity, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

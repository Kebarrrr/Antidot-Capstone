package com.capstone.antidot.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R
import com.capstone.antidot.api.models.DiagnosisRequest
import com.capstone.antidot.api.models.Symptom

class DiagnosisActivity : AppCompatActivity() {

    private lateinit var adapter: DiagnosisAdapter
    private lateinit var symptoms: MutableList<Symptom>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnosis)

        // Data awal (symptom bawaan)
        symptoms = mutableListOf(
            Symptom("Gejala 1"),
            Symptom("Gejala 2"),
            Symptom("Gejala 3")
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
            val newSymptom = Symptom("Gejala ${symptoms.size + 1}", true)
            symptoms.add(newSymptom)
            adapter.notifyItemInserted(symptoms.size - 1)
        }
    }
}



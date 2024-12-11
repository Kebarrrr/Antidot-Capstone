package com.capstone.antidot.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R
import com.capstone.antidot.api.models.Symptom

class DiagnosisAdapter(
    private val symptoms: MutableList<Symptom>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder>() {

    inner class DiagnosisViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val gejalaTextView: TextView = view.findViewById(R.id.gejala_1)
        val diagnosisEditText: EditText = view.findViewById(R.id.diagnosis_1)
        val deleteButton: ImageButton = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diagnosis, parent, false)
        return DiagnosisViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        val symptom = symptoms[position]
        holder.gejalaTextView.text = symptom.symptoms[0]
        holder.diagnosisEditText.hint = "Enter diagnosis for ${symptom.symptoms[0]}"

        // Tampilkan atau sembunyikan tombol "Delete"
        if (symptom.isUserAdded) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                onDeleteClick(position) // Callback untuk hapus item
            }
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = symptoms.size

    fun removeSymptom(position: Int) {
        symptoms.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, symptoms.size)
    }
}
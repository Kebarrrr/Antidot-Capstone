package com.capstone.antidot.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.R
import com.capstone.antidot.api.models.Symptoms
import com.capstone.antidot.api.models.SymptomsRequest

class DiagnosisAdapter(
    private val symptoms: MutableList<Symptoms>,
    private val onRemoveClick: (position: Int) -> Unit
) : RecyclerView.Adapter<DiagnosisAdapter.DiagnosisViewHolder>() {

    private val userInputs = mutableListOf<String>()
    private val limitedSymptoms = symptoms.take(3).toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiagnosisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diagnosis, parent, false)
        return DiagnosisViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiagnosisViewHolder, position: Int) {
        val symptom = limitedSymptoms[position]
        val autoCompleteTextView = holder.itemView.findViewById<AutoCompleteTextView>(R.id.autoCompleteDiagnosis)

        val symptomNames = symptoms.map { it.symptomName }
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_dropdown_item_1line, symptomNames)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setText(symptom.symptomName, false)

        autoCompleteTextView.addTextChangedListener { editable ->
            symptom.symptomName = editable.toString()

            // Mark the symptom as selected when the user starts typing
            if (editable != null) {
                if (editable.isNotEmpty()) {
                    symptom.isSelected = true
                }
            }

            // Save the user input in the list
            if (position < userInputs.size) {
                userInputs[position] = editable.toString()
            } else {
                userInputs.add(editable.toString())
            }
        }


        val btnRemoveSymptom: AppCompatImageButton = holder.itemView.findViewById(R.id.btn_delete)
        btnRemoveSymptom.setOnClickListener {
            onRemoveClick(position)
            userInputs.removeAt(position)

            if (limitedSymptoms.size < 3) {
                limitedSymptoms.add(Symptoms(""))
                notifyItemInserted(limitedSymptoms.size - 1)
            }
        }
    }

    fun getUserInputs(): List<String> {
        val userInputs = mutableListOf<String>()
        // Loop through symptoms and add only those that are selected and not empty
        for (symptom in symptoms) {
            if (symptom.symptomName.isNotEmpty() && symptom.isSelected) {
                userInputs.add(symptom.symptomName)
            }
        }
        return userInputs
    }


    override fun getItemCount(): Int {
        return limitedSymptoms.size
    }

    class DiagnosisViewHolder(view: View) : RecyclerView.ViewHolder(view)
}

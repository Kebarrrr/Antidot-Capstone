package com.capstone.antidot.ui.Reminder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.antidot.databinding.ActivityAddReminderBinding
import com.capstone.antidot.ui.Antibiotics.AntibioticsViewModel
import com.capstone.antidot.ui.Antibiotics.ViewModelFactory
import kotlinx.coroutines.launch


class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReminderBinding
    private lateinit var antibioticsViewModel: AntibioticsViewModel
    private lateinit var adapter: AddReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        // Setup ViewModel
        antibioticsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(this)
        )[AntibioticsViewModel::class.java]

        // Setup RecyclerView
        adapter = AddReminderAdapter { selectedEvent ->
            if (selectedEvent.antibioticID != 0) {
                val intent = Intent(this, AddReminderByDatabaseActivity::class.java)
                intent.putExtra("EVENT_ID", selectedEvent.antibioticID.toString())
                Log.d("EventAdapter", "Selected Event ID: ${selectedEvent.antibioticID}")
                startActivity(intent)
            } else {
                Log.d("EventAdapter", "Invalid Antibiotic ID: ${selectedEvent.antibioticID}")
            }
        }

        binding.rvReminder.layoutManager = LinearLayoutManager(this)
        binding.rvReminder.adapter = adapter

        // Observasi data dari ViewModel
        antibioticsViewModel.events.observe(this) { events ->
            adapter.submitList(events)
        }

        antibioticsViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        antibioticsViewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }

        // Hubungkan SearchView dengan Adapter
        val searchView: SearchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText)
                return true
            }
        })

        // Ambil data dari ViewModel
        lifecycleScope.launch {
            antibioticsViewModel.getAntibiotics()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}



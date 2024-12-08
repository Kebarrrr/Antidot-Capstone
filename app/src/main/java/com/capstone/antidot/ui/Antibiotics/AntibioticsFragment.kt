package com.capstone.antidot.ui.Antibiotics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.antidot.databinding.FragmentAntibioticsBinding
import com.capstone.antidot.ui.Antibiotics.Adapter.EventAdapter
import com.capstone.antidot.ui.Antibiotics.DetailAntibiotics.DetailActivity
import kotlinx.coroutines.launch

class AntibioticsFragment : Fragment() {

    private lateinit var binding: FragmentAntibioticsBinding
    private lateinit var antibioticsViewModel: AntibioticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAntibioticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        antibioticsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext())
        )[AntibioticsViewModel::class.java]

        binding.rvAntibiotics.layoutManager = LinearLayoutManager(context)

        /*      untuk ke detail antibiotic*/
        val adapter = EventAdapter() { selectedEvent ->
            if (selectedEvent.antibioticID != 0) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("EVENT_ID", selectedEvent.antibioticID.toString())
                Log.d("EventAdapter", "Selected Event ID: ${selectedEvent.antibioticID}")
                startActivity(intent)
            } else {
                Log.d("EventAdapter", "Invalid Antibiotic ID: ${selectedEvent.antibioticID}")
            }
        }

        binding.rvAntibiotics.adapter = adapter

        antibioticsViewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        antibioticsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        antibioticsViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }

        lifecycleScope.launch {
            antibioticsViewModel.getAntibiotics()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
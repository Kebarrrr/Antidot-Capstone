package com.capstone.antidot.ui.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.capstone.antidot.R
import com.capstone.antidot.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe ViewModel data
        homeViewModel.greetingText.observe(viewLifecycleOwner) { greeting ->
            binding.greetingName.text = greeting
        }

        homeViewModel.askText.observe(viewLifecycleOwner) { ask ->
            binding.ask.text = ask
        }

        // Set up click listeners for cards
        /*setupCardClickListeners()*/
    }

    /*private fun setupCardClickListeners() {
        binding.diagnosisCard.setOnClickListener { view ->
            //Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_diagnosisFragment)
        }

        binding.historyDiseaseCard.setOnClickListener { view ->
            //Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_historyDiseaseFragment)
        }

        binding.medicineHistoryCard.setOnClickListener { view ->
            //Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_medicineHistoryFragment)
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

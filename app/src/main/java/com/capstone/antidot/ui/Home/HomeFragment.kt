package com.capstone.antidot.ui.Home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.capstone.antidot.R
import com.capstone.antidot.api.models.UserResponse
import com.capstone.antidot.databinding.FragmentHomeBinding
import com.capstone.antidot.ui.DiagnosisActivity

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe live data from the ViewModel
        homeViewModel.userProfile.observe(viewLifecycleOwner, Observer { userResponse ->
            updateUI(userResponse)
        })

        // Trigger user profile loading
        homeViewModel.fetchUserProfile()

        setupButtonClickListeners()
    }

    private fun updateUI(userResponse: UserResponse?) {
        // Update UI with the fetched user data
        userResponse?.let {
            binding.greetingName.text = getString(R.string.hiusername, it.user.fullName)
            // You can load profile image using Glide or Picasso
            Glide.with(this).load(it.user.profilePicture).into(binding.profileImage)
        }
    }

    private fun setupButtonClickListeners() {
        // Handle button clicks
        binding.diagnosisCard.setOnClickListener {
            onDiagnosisButtonClick()
        }

        // Add other button click listeners as needed
        // For example:
        binding.historyDiseaseCard.setOnClickListener {
            onHistoryDiseaseButtonClick()
        }

        binding.medicineHistoryCard.setOnClickListener {
            onMedicineHistoryButtonClick()
        }
    }

    private fun onDiagnosisButtonClick() {
        // Start DiagnosisActivity when the diagnosis button is clicked
        activity?.let { context ->
            val intent = Intent(context, DiagnosisActivity::class.java)
            startActivity(intent)
        }
    }

    private fun onHistoryDiseaseButtonClick() {
        // Handle history disease button click
    }

    private fun onMedicineHistoryButtonClick() {
        // Handle medicine history button click
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
        }
}
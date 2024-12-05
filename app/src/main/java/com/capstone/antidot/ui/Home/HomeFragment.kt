package com.capstone.antidot.ui.Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.antidot.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observe the profile data
        homeViewModel.userProfile.observe(viewLifecycleOwner) { user ->
            // Update UI with user profile data
            binding.greetingName.text = "Hi, ${user.fullName}"
            Glide.with(this)
                .load(user.profilePicture)
                .placeholder(android.R.drawable.ic_menu_camera) // Default placeholder
                .into(binding.profileImage)
        }

        // Call ViewModel to load the user profile
        homeViewModel.loadUserProfile()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

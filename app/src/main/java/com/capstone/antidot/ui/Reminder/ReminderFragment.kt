package com.capstone.antidot.ui.Reminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.capstone.antidot.R
import com.capstone.antidot.databinding.FragmentReminderBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val reminderViewModel =
            ViewModelProvider(this).get(ReminderViewModel::class.java)

        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Menyambungkan FAB dengan ID di layout
        val fabAddReminder: FloatingActionButton = binding.fabAddReminder

        // Mengatur listener untuk FAB
        fabAddReminder.setOnClickListener { view ->
            // Intent untuk membuka AddReminderActivity
            val intent = Intent(requireContext(), AddReminderActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
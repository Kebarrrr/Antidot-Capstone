package com.capstone.antidot.ui.Reminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.antidot.databinding.FragmentReminderBinding
import com.capstone.antidot.api.models.RemindersItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private lateinit var reminderViewModel: ReminderViewModel
    private lateinit var reminderAdapter: ReminderAdapter
    private val reminderList = mutableListOf<RemindersItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)

        val recyclerView: RecyclerView = binding.rvReminder
        recyclerView.layoutManager = LinearLayoutManager(context)
        reminderAdapter = ReminderAdapter(reminderList, requireContext())
        recyclerView.adapter = reminderAdapter
        val progressBar: ProgressBar = binding.progressBar
        reminderViewModel = ViewModelProvider(this, ReminderViewModelFactory(requireContext())).get(ReminderViewModel::class.java)
        reminderViewModel.reminders.observe(viewLifecycleOwner, Observer { reminders ->
            reminderList.clear()
            reminderList.addAll(reminders)
            reminderAdapter.updateList(reminders)
            progressBar.visibility = View.GONE
        })
        reminderViewModel.fetchReminders()
        progressBar.visibility = View.VISIBLE
        val fabAddReminder: FloatingActionButton = binding.fabAddReminder
        fabAddReminder.setOnClickListener {
            val intent = Intent(requireContext(), AddReminderActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

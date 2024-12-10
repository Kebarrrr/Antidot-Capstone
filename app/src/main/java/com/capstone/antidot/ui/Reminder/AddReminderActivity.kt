package com.capstone.antidot.ui.Reminder

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.antidot.R
import com.capstone.antidot.databinding.ActivityAddReminderBinding

class AddReminderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddReminderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnBack.setOnClickListener {
            onBackPressed() // Atau bisa pakai finish() jika ingin langsung selesai
        }
        // Menyambungkan SearchView
        val searchView: SearchView = findViewById(R.id.searchView)

        // Mengatur listener untuk pencarian
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Menangani aksi saat teks pencarian disubmit
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Menangani aksi saat teks pencarian berubah
                return false
            }
        })
    }
}
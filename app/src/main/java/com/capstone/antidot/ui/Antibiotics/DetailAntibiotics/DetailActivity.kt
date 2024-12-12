package com.capstone.antidot.ui.Antibiotics.DetailAntibiotics

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.AntibioticsItem
import com.capstone.antidot.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    private var currentAntibiotic: AntibioticsItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menggunakan ViewModelFactory dengan Application context
        detailViewModel = ViewModelProvider(
            this,
            DetailViewModelFactory(application) // Mengirimkan application context
        )[DetailViewModel::class.java]

        val antibioticId = intent.getStringExtra("EVENT_ID")
        Log.d("DetailActivity", "Received antibiotic ID: $antibioticId")

        if (antibioticId.isNullOrEmpty()) {
            showError("Antibiotic ID not found")
            finish()
            return
        }

        setupObservers()
        detailViewModel.fetchDetailAntibiotics(antibioticId)
        // Menangani klik pada tombol kembali
        binding.btnBack.setOnClickListener {
            onBackPressed() // Atau bisa pakai finish() jika ingin langsung selesai
        }
    }

    private fun setupObservers() {
        detailViewModel.antibioticDetail.observe(this) { antibiotic ->
            antibiotic?.let {
                currentAntibiotic = it
                displayAntibioticDetails(it)
            }
        }

        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        detailViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let { showError(it) }
        }
    }

    private fun displayAntibioticDetails(antibiotic: AntibioticsItem) {
        binding.apply {
            tvItemName.text = antibiotic.antibioticsName
            tvPharmacologicalTherapy.text = antibiotic.others
            isiDosis.text = antibiotic.antibioticsDosage
            isiDosisPerhari.text = antibiotic.antibioticFrequencyUsagePerDay
            isiDeskripsi.text = antibiotic.antibioticsDescription
            isiDuration.text = antibiotic.antibioticTotalDaysOfUsage
            isiUsage.text = antibiotic.antibioticsUsage


            Glide.with(this@DetailActivity)
                .load(antibiotic.antibioticImage)
                .into(imgItemPhoto)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
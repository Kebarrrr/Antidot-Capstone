package com.capstone.antidot.ui.Home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.ArticleItem
import com.capstone.antidot.api.models.UserResponse
import com.capstone.antidot.databinding.FragmentHomeBinding
import com.capstone.antidot.ui.DiagnosisActivity
import com.capstone.antidot.ui.LoginActivity
import com.capstone.antidot.ui.NewsAdapter
import com.capstone.antidot.utils.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var recyclerView: RecyclerView
    private val articleList = mutableListOf<ArticleItem>()


    private lateinit var sessionManager: SessionManager
    private lateinit var btnLogout: ImageButton

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

        // Inisialisasi SessionManager
        sessionManager = SessionManager(requireContext())

        // Ambil referensi ke tombol logout
        btnLogout = view.findViewById(R.id.btn_logout)

        // Set OnClickListener untuk tombol logout
        btnLogout.setOnClickListener {
            // Tampilkan dialog konfirmasi logout
            showLogoutConfirmationDialog()
        }

        recyclerView = view.findViewById(R.id.rv_news)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        newsAdapter = NewsAdapter(articleList)
        recyclerView.adapter = newsAdapter

        // Fetch news from the API
        fetchNews()
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
    // Fungsi untuk menampilkan konfirmasi logout
    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah Anda yakin ingin keluar dari akun?")
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                // Clear session
                sessionManager.clearSession()

                // Tampilkan Toast konfirmasi logout
                Toast.makeText(requireContext(), "Anda berhasil logout", Toast.LENGTH_SHORT).show()

                // Navigasi ke LoginActivity
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)

                // Finish HomeActivity agar pengguna tidak bisa kembali ke halaman ini
                activity?.finish()
            }
            .setNegativeButton("Tidak") { dialog, id ->
                // Jika memilih tidak, tutup dialog
                dialog.dismiss()
            }

        // Tampilkan dialog
        val alert = builder.create()
        alert.show()
    }

    private fun fetchNews() {
        val apiService = RetrofitClient.getInstance(requireContext())

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.getArticle()
                if (response.status == "success") {
                    articleList.clear()
                    articleList.addAll(response.article)
                    newsAdapter.notifyDataSetChanged()  // Update RecyclerView
                } else {
                    Toast.makeText(context, "Failed to load news", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
        }
}
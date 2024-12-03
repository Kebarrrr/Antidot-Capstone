package com.capstone.antidot.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameField: EditText = findViewById(R.id.ed_register_name)
        val dateField: EditText = findViewById(R.id.ed_birth_date)
        val emailField: EditText = findViewById(R.id.ed_register_email)
        val passwordField: EditText = findViewById(R.id.ed_register_password)
        val confirmPasswordField: EditText = findViewById(R.id.ed_confirm_password)
        val registerButton: Button = findViewById(R.id.btn_register)
        val goToLoginText: TextView = findViewById(R.id.tv_go_to_login)

        // Register button click listener
        registerButton.setOnClickListener {
            handleRegister(
                name = nameField.text.toString(),
                date = dateField.text.toString(),
                email = emailField.text.toString(),
                password = passwordField.text.toString(),
                confirmPassword = confirmPasswordField.text.toString()
            )
        }

        // Navigation to login page
        goToLoginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handleRegister(name: String, date: String, email: String, password: String, confirmPassword: String) {
        if (!validateInput(name, date, email, password, confirmPassword)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.register(
                    RegisterRequest(name, date, email, password, confirmPassword)
                )
                withContext(Dispatchers.Main) {
                    handleResponse(response.success, response.message)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun validateInput(name: String, date: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showToast("Semua field harus diisi")
            return false
        }

        if (password != confirmPassword) {
            showToast("Password dan Konfirmasi Password harus sama")
            return false
        }

        if (!date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            showToast("Format tanggal harus YYYY-MM-DD")
            return false
        }

        return true
    }

    private fun handleResponse(success: Boolean, message: String) {
        if (success) {
            showToast("Registrasi berhasil. Silakan login")
            finish() // Kembali ke aktivitas sebelumnya
        } else {
            showToast(message)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

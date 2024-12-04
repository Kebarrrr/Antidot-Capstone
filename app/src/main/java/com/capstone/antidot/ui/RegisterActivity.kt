package com.capstone.antidot.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.RegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

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

        // Set up Date Picker Dialog
        dateField.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    dateField.setText(String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Handle Registration
        registerButton.setOnClickListener {
            val birthDate = convertToDate(dateField.text.toString())
            if (birthDate != null) {
                handleRegister(
                    fullName = nameField.text.toString(),
                    birthDate = birthDate,
                    email = emailField.text.toString(),
                    password = passwordField.text.toString(),
                    confPassword = confirmPasswordField.text.toString()
                )
            } else {
                showToast("Format tanggal harus YYYY-MM-DD")
            }
        }

        // Navigate to Login
        goToLoginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun handleRegister(fullName: String, birthDate: Date, email: String, password: String, confPassword: String) {
        if (!validateInput(fullName, birthDate, email, password, confPassword)) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.register(
                    RegisterRequest(fullName, birthDate, email, password, confPassword)
                )
                withContext(Dispatchers.Main) {
                    if (response.status == "success") {
                        showToast("Registrasi berhasil. Silakan login")
                        navigateToLogin()
                    } else {
                        showToast(response.message)
                    }
                }
            } catch (e: retrofit2.HttpException) {
                withContext(Dispatchers.Main) {
                    val errorBody = e.response()?.errorBody()?.string()
                    showToast("Error: ${errorBody ?: "Terjadi kesalahan"}")
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun validateInput(fullName: String, birthDate: Date, email: String, password: String, confPassword: String): Boolean {
        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confPassword.isBlank()) {
            showToast("Semua field harus diisi")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Format email tidak valid")
            return false
        }

        if (password != confPassword) {
            showToast("Password dan Konfirmasi Password harus sama")
            return false
        }

        if (password.length < 8) {
            showToast("Password harus memiliki minimal 8 karakter")
            return false
        }

        return true
    }

    private fun convertToDate(dateString: String): Date? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.parse(dateString)
        } catch (e: Exception) {
            null
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

    override fun onBackPressed() {
        navigateToLogin()
    }
}

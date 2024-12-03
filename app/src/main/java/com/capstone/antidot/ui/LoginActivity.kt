package com.capstone.antidot.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.antidot.R
import com.capstone.antidot.api.RetrofitClient
import com.capstone.antidot.api.models.UserRequest
import com.capstone.antidot.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField: EditText = findViewById(R.id.ed_login_email)
        val passwordField: EditText = findViewById(R.id.ed_login_password)
        val loginButton: Button = findViewById(R.id.btn_login)
        val sessionManager = SessionManager(this)

        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.instance.login(UserRequest(email, password))
                    withContext(Dispatchers.Main) {
                        if (response.status == "success") {
                            val token = response.token
                            if (token.isNullOrEmpty()) {
                                Toast.makeText(this@LoginActivity, "Token tidak valid", Toast.LENGTH_SHORT).show()
                                return@withContext
                            }
                            sessionManager.saveSession(token)
                            Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, response.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Tidak dapat terhubung ke server. Periksa koneksi Anda.", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Error tidak diketahui: ${e.message}", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
        }

        val goToRegisterText: TextView = findViewById(R.id.tv_go_to_register)
        goToRegisterText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
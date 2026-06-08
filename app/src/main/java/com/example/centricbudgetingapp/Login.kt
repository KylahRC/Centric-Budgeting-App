package com.example.centricbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)
        val emailField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.email)
        val passwordField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val registerNow = findViewById<TextView>(R.id.registerNow)

        registerNow.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }

        btnLogin.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // If user is already logged in, skip the login screen
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
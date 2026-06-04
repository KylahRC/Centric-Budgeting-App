@file:Suppress("DEPRECATION")

package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import android.app.DatePickerDialog

class Register : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private lateinit var nameField: TextInputEditText
    private lateinit var usernameField: TextInputEditText
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var confirmPasswordField: TextInputEditText
    private lateinit var dobField: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loginNow: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        nameField = findViewById(R.id.name)
        usernameField = findViewById(R.id.username)
        emailField = findViewById(R.id.email)
        passwordField = findViewById(R.id.password)
        confirmPasswordField = findViewById(R.id.confirmPassword)
        dobField = findViewById(R.id.dob)
        registerButton = findViewById(R.id.btn_register)
        loginNow = findViewById(R.id.loginNow)
    }

    private fun setupListeners() {
        loginNow.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }

        // Show DatePicker when DOB field is tapped
        dobField.setOnClickListener { showDatePicker() }

        registerButton.setOnClickListener { registerUser() }
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // spinner style
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dobField.setText(formatted)
            },
            year,
            month,
            day
        )

        // Force spinner mode
        datePicker.datePicker.calendarViewShown = false
        datePicker.datePicker.spinnersShown = true

        datePicker.show()
    }


    private fun registerUser() {
        val name = nameField.text.toString().trim()
        val username = usernameField.text.toString().trim()
        val email = emailField.text.toString().trim()
        val password = passwordField.text.toString().trim()
        val confirmPassword = confirmPasswordField.text.toString().trim()
        val dob = dobField.text.toString().trim()

        if (!validateInputs(name, username, email, password, confirmPassword, dob)) return

        progressBar.visibility = View.VISIBLE

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser?.uid
                    if (uid != null) {
                        FirebaseInteractions.createUserDoc(
                            uid,
                            name,
                            username,
                            email,
                            dob
                        ) { success ->
                            if (success) {
                                // Run these independently
                                FirebaseInteractions.createStarterCategory(uid)
                                FirebaseInteractions.createStarterExpense(uid)

                                Toast.makeText(
                                    this,
                                    "Account created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to create user data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
    }



    private fun validateInputs(
        name: String,
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
        dob: String
    ): Boolean {
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}

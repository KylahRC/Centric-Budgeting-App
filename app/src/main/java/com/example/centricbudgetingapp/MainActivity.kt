package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {
    var auth: FirebaseAuth? = null
    var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser


        if (user == null) {
            // If no one is logged in, send to Login screen
            startActivity(Intent(this, Login::class.java))
            finish()
        } else {
            // If someone is already logged in, send to Home screen
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
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
    var button1: Button? = null
    var button2: Button? = null
    var textView: TextView? = null
    var user: FirebaseUser? = null
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    //val userRef = db.collection("users").document(userId!!)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        button1 = findViewById<Button>(R.id.logout)
        button2 = findViewById<Button>(R.id.dont_log_out)
        textView = findViewById<TextView>(R.id.user_details)
        user = auth!!.getCurrentUser()

        if (user == null) {
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(getApplicationContext(), HomeActivity::class.java)
            startActivity(intent)
            finish()
            textView!!.setText(user!!.getEmail())
        }
        button1?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(getApplicationContext(), Login::class.java)
                startActivity(intent)
                finish()
            }
        })
        button2?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(getApplicationContext(), HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

    }
}

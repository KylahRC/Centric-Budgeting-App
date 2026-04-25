package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
    var auth: FirebaseAuth? = null
    var button: Button? = null
    var textView: TextView? = null
    var user: FirebaseUser? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        button = findViewById<Button>(R.id.logout)
        textView = findViewById<TextView>(R.id.user_details)
        user = auth!!.getCurrentUser()

        if (user == null) {
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        } else {
            textView!!.setText(user!!.getEmail())
        }
        button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(getApplicationContext(), Login::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}

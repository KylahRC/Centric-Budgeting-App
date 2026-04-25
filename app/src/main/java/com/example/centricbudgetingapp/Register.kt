package com.example.centricbudgetingapp

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

//import androidx.activity.enableEdgeToEdge;
class Register : AppCompatActivity() {
    //Declare the object
    var editTextEmail: TextInputEditText? = null
    var editTextPassword: TextInputEditText? = null
    var buttonReg: Button? = null
    var mAuth: FirebaseAuth? = null
    var progressBar: ProgressBar? = null
    var textView: TextView? = null

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.getCurrentUser()
        if (currentUser != null) {
            val intent = Intent(getApplicationContext(), MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()

        // Initialise the objects
        editTextEmail = findViewById<TextInputEditText>(R.id.email)
        editTextPassword = findViewById<TextInputEditText>(R.id.password)
        buttonReg = findViewById<Button>(R.id.btn_register)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        textView = findViewById<TextView>(R.id.loginNow)
        textView!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(getApplicationContext(), Login::class.java)
                startActivity(intent)
                finish()
            }
        })
        buttonReg!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                progressBar!!.setVisibility(View.VISIBLE)
                val email: String?
                val password: String?
                email = editTextEmail!!.getText().toString().toString()
                password = editTextPassword!!.getText().toString().toString()

                if (TextUtils.isEmpty(email)) {
                    val context: Context?
                    val text: String?
                    Toast.makeText(
                        this@Register.also { context = it },
                        "Enter email".also { text = it },
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (TextUtils.isEmpty(password)) {
                    val context: Context?
                    val text: String?
                    Toast.makeText(
                        this@Register.also { context = it },
                        "Enter Password".also { text = it },
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                mAuth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(object : OnCompleteListener<AuthResult?> {
                        override fun onComplete(task: Task<AuthResult?>) {
                            progressBar!!.setVisibility(View.GONE)
                            if (task.isSuccessful()) {
                                val context: Context?
                                var text: String?
                                Toast.makeText(
                                    this@Register.also { context = it }, "Account Created.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // If sign in fails, display a message to the user.
                                val context: Context?
                                var text: String?
                                Log.w(
                                    ContentValues.TAG,
                                    "createUserWithEmail:failure",
                                    task.getException()
                                )
                                Toast.makeText(
                                    this@Register.also { context = it }, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        })
    }
}
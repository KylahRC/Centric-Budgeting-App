package com.example.centricbudgetingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SplitExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_expense)

        // Retrieve extras
        val totalAmount = intent.getDoubleExtra("EXTRA_TOTAL", 0.0)
        val expenseId = intent.getStringExtra("EXTRA_ID")

        // Initialize views
        val tvTotal = findViewById<TextView>(R.id.tvTotalAmount)
        val etMyShare = findViewById<EditText>(R.id.etMyShare)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmSplit)

        // Defensive check
        if (tvTotal == null || etMyShare == null || btnConfirm == null) {
            Log.e("SplitActivity", "Critical Error: Views not found in XML!")
            Toast.makeText(this, "Layout error!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Setup UI
        tvTotal.text = "Total Expense: R$totalAmount"

        btnConfirm.setOnClickListener {
            val shareInputStr = etMyShare.text.toString()
            val shareInput = shareInputStr.toDoubleOrNull()

            if (shareInputStr.isEmpty()) {
                etMyShare.error = "Please enter an amount"
                return@setOnClickListener
            }

            if (shareInput != null && shareInput > 0 && shareInput <= totalAmount) {
                if (expenseId != null) {
                    FirebaseInteractions.updateExpenseAmount(expenseId, shareInput) { success ->
                        if (success) {
                            Toast.makeText(this, "Split successful!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this, "Database update failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Error: Expense ID missing.", Toast.LENGTH_SHORT).show()
                }
            } else {
                etMyShare.error = "Enter a value between 0 and R$totalAmount"
            }
        }
    }
}
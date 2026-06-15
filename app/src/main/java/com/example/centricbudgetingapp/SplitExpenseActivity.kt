package com.example.centricbudgetingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class SplitExpenseActivity : AppCompatActivity() {

    // UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split_expense)

        // --- Setup UI ---
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)

        // --- Drawer Menu ---
        setupDrawer()


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

    private fun setupDrawer() {
        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_add_category -> startActivity(Intent(this, AddCategoryActivity::class.java))
                // R.id.nav_add_expense -> startActivity(Intent(this, AddExpenseActivity::class.java))
                R.id.nav_budget_goals -> startActivity(Intent(this, BudgetGoalsActivity::class.java))
                //  R.id.nav_view_expenses -> startActivity(Intent(this, ViewExpensesActivity::class.java))
                R.id.nav_category_totals -> startActivity(Intent(this, CategoryTotalsActivity::class.java))
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
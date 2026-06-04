package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.text.InputType
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupBalanceButton()
        fetchUserData()
        setupDrawer()
    }

    //Fetches user data from Firebase using the firebase interactions file
    @SuppressLint("SetTextI18n")
    private fun fetchUserData() {
        FirebaseInteractions.getBalance { balance ->
            findViewById<TextView>(R.id.tvBalance).text = "Balance: ${balance ?: 0}"
        }

        FirebaseInteractions.getUsername { username ->
            findViewById<TextView>(R.id.tvTest).text = "Connected as: ${username ?: "Unknown"}"
        }
    }




    //Handles the button for adding to the balance
    @SuppressLint("SetTextI18n")
    private fun setupBalanceButton() {
        val addBalanceBtn = findViewById<Button>(R.id.btnAddBalance)
        addBalanceBtn.setOnClickListener {
            val input = EditText(this).apply {
                inputType = InputType.TYPE_CLASS_NUMBER
            }

            AlertDialog.Builder(this)
                .setTitle("Add to Balance")
                .setView(input)
                .setPositiveButton("Add") { _, _ ->
                    val amount = input.text.toString().toLongOrNull() ?: 0L
                    if (amount > 0) {
                        FirebaseInteractions.addToBalance(amount) { newBalance ->
                            if (newBalance != null) {
                                findViewById<TextView>(R.id.tvBalance).text = "Balance: $newBalance"
                            } else {
                                Toast.makeText(this, "Failed to update balance", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }



    //nav menu
    @SuppressLint("SetTextI18n")
    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)

        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_add_category -> startActivity(Intent(this, AddCategoryActivity::class.java))
                R.id.nav_add_expense -> startActivity(Intent(this, AddExpenseActivity::class.java))
                R.id.nav_budget_goals -> startActivity(Intent(this, BudgetGoalsActivity::class.java))
                R.id.nav_view_expenses -> startActivity(Intent(this, ViewExpensesActivity::class.java))
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

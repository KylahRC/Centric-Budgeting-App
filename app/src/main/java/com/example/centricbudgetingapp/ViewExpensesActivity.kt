package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class ViewExpensesActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var expenseListText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

        setupDrawer()

        expenseListText = findViewById(R.id.tvExpenseList)
        loadExpenses()
    }

    private fun setupDrawer() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_add_category -> startActivity(Intent(this, AddCategoryActivity::class.java))
                R.id.nav_add_expense -> startActivity(Intent(this, AddExpenseActivity::class.java))
                R.id.nav_budget_goals -> startActivity(Intent(this, BudgetGoalsActivity::class.java))
                R.id.nav_view_expenses -> startActivity(Intent(this, ViewExpensesActivity::class.java))
                R.id.nav_category_totals -> startActivity(Intent(this, CategoryTotalsActivity::class.java))
                R.id.nav_split_expense -> startActivity(Intent(this, SplitExpenseActivity::class.java))
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

    @SuppressLint("SetTextI18n")
    private fun loadExpenses() {
        FirebaseInteractions.getCategories { categories ->
            val categoryMap = categories.associate { it.id to (it.name ?: "Unnamed") }

            FirebaseInteractions.getExpenses { expenses ->
                if (expenses.isEmpty()) {
                    expenseListText.text = "No expenses found."
                } else {
                    val builder = StringBuilder()
                    builder.append("Tap the text below to split the last expense:\n\n")
                    for (exp in expenses) {
                        val categoryName = categoryMap[exp.categoryId] ?: "Unknown"
                        builder.append("• ${exp.description ?: "No description"}\n")
                        builder.append("  Amount: R${exp.amount ?: 0.0}\n\n")
                    }
                    expenseListText.text = builder.toString()

                    expenseListText.setOnClickListener {
                        if (expenses.isNotEmpty()) {
                            val lastExpense = expenses.last()
                            val intent = Intent(this, SplitExpenseActivity::class.java)
                            intent.putExtra("EXTRA_TOTAL", lastExpense.amount ?: 0.0)
                            intent.putExtra("EXTRA_ID", lastExpense.id)
                            startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}
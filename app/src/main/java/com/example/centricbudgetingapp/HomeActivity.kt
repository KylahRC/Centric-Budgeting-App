package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.icu.util.Calendar
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.text.InputType
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import android.graphics.Typeface
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import android.widget.ImageView // Needed for badgeIcon
import android.view.View        // Needed for View.VISIBLE

class HomeActivity : AppCompatActivity() {

    // UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var balanceAmount: TextView
    private lateinit var moneyRemainingAmount: TextView
    private lateinit var recentExpensesLayout: LinearLayout
    private lateinit var addExpenseButton: Button

    private lateinit var pieChart: PieChart

    private lateinit var badgeIcon: ImageView

    // Data
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //  Setup UI
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)
        balanceAmount = findViewById(R.id.tvBalanceAmount)
        moneyRemainingAmount = findViewById(R.id.tvMoneyRemainingAmount)
        recentExpensesLayout = findViewById(R.id.layoutRecentExpenses)
        addExpenseButton = findViewById(R.id.btnAddExpense)
        pieChart = findViewById(R.id.pieChart)
        badgeIcon = findViewById(R.id.ivBadge)

        // Setup Chart
        setupPieChart()

        // Drawer Menu
        setupDrawer()

        // Load Data
        loadBalance()
        loadMoneyRemaining(currentYear, currentMonth)
        loadRecentExpenses(currentYear, currentMonth)

        // Add Expense Button
        addExpenseButton.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    // Helper function for Neo-Minimalist chart
    private fun setupPieChart() {
        // Dummy data for now - we will connect this to real category totals later
        val entries = listOf(
            PieEntry(50f, "Food"),
            PieEntry(50f, "Rent")
        )

        val dataSet = PieDataSet(entries, "Expenses")
        // Using clean colors for the Neo-Minimalist look
        dataSet.colors = listOf(0xFF6200EE.toInt(), 0xFF03DAC6.toInt())

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false // Removes 'Description' text
        pieChart.invalidate()
    }

    private fun setupDrawer() {
        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
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

    private fun loadBalance() {
        FirebaseInteractions.getBalance { balance ->
            balanceAmount.text = "R${balance ?: 0}"
        }
    }

    private fun loadMoneyRemaining(year: Int, month: Int) {
        FirebaseInteractions.getBalance { balance ->
            FirebaseInteractions.getCategoryTotalsForMonth(year, month) { totals ->
                val totalExpenses = totals.values.sum()
                val remaining = (balance ?: 0) - totalExpenses
                moneyRemainingAmount.text = "R$remaining"
            }
        }
    }

    private fun loadRecentExpenses(year: Int, month: Int) {
        FirebaseInteractions.getExpenses { expenses ->
            val filtered = expenses.filter { exp ->
                exp.date?.let {
                    val parts = it.split("-")
                    val expYear = parts.getOrNull(0)?.toIntOrNull()
                    val expMonth = parts.getOrNull(1)?.toIntOrNull()
                    expYear == year && expMonth == month
                } ?: false
            }.sortedByDescending { it.date }.take(5) // latest 5

            recentExpensesLayout.removeAllViews()

            for (exp in filtered) {
                val expLayout = LinearLayout(this)
                expLayout.orientation = LinearLayout.VERTICAL
                expLayout.setPadding(0, 8, 0, 8)

                val descText = TextView(this)
                descText.text = "${exp.date ?: ""} – ${exp.description ?: "No description"}"
                descText.setTypeface(null, Typeface.BOLD)
                expLayout.addView(descText)

                val amountText = TextView(this)
                amountText.text = "Amount: R${exp.amount ?: 0.0}"
                expLayout.addView(amountText)

                recentExpensesLayout.addView(expLayout)
            }
        }
    }
    private fun checkBadges(expenseCount: Int) {
        if (expenseCount >= 10) {

            badgeIcon.visibility = View.VISIBLE
        }
    }
}


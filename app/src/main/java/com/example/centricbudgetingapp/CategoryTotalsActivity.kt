package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
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
import java.util.Calendar
import android.app.DatePickerDialog
import android.graphics.Typeface
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout
import java.text.DateFormatSymbols
import android.widget.Space

class CategoryTotalsActivity : AppCompatActivity() {

    // UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var monthHeading: TextView
    private lateinit var prevButton: ImageButton
    private lateinit var nextButton: ImageButton
    private lateinit var totalsLayout: LinearLayout

    // Data
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_totals)

        // --- Setup UI ---
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)
        monthHeading = findViewById(R.id.tvMonthHeading)
        prevButton = findViewById(R.id.btnPrevMonth)
        nextButton = findViewById(R.id.btnNextMonth)
        totalsLayout = findViewById(R.id.layoutTotals)

        // --- Drawer Menu ---
        setupDrawer()

        // --- Load Current Month Totals ---
        updateMonthHeading()
        loadCategoryTotals(currentYear, currentMonth)

        // --- Arrow Navigation ---
        prevButton.setOnClickListener { previousMonth() }
        nextButton.setOnClickListener { nextMonth() }
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

    private fun updateMonthHeading() {
        val monthName = DateFormatSymbols().months[currentMonth - 1]
        monthHeading.text = "$monthName $currentYear"
    }

    private fun loadCategoryTotals(year: Int, month: Int) {
        FirebaseInteractions.getCategories { categories ->
            val categoryMap = categories.associate { it.id to (it.name ?: "Unnamed") }

            FirebaseInteractions.getCategoryTotalsForMonth(year, month) { totals ->
                totalsLayout.removeAllViews()

                if (totals.isEmpty()) {
                    val emptyText = TextView(this)
                    emptyText.text = "No expenses found"
                    totalsLayout.addView(emptyText)
                } else {
                    for ((catId, total) in totals) {
                        val catName = categoryMap[catId] ?: "Unknown"

                        // Category heading
                        val heading = TextView(this)
                        heading.text = catName
                        heading.textSize = 18f
                        heading.setTypeface(null, Typeface.BOLD)
                        totalsLayout.addView(heading)

                        // Expense total
                        val totalText = TextView(this)
                        totalText.text = "Total: R$total"
                        totalsLayout.addView(totalText)

                        // Add spacing
                        val spacer = Space(this)
                        spacer.minimumHeight = 24
                        totalsLayout.addView(spacer)
                    }
                }
            }
        }
    }

    private fun nextMonth() {
        if (currentMonth == 12) {
            currentMonth = 1
            currentYear++
        } else {
            currentMonth++
        }
        updateMonthHeading()
        loadCategoryTotals(currentYear, currentMonth)
    }

    private fun previousMonth() {
        if (currentMonth == 1) {
            currentMonth = 12
            currentYear--
        } else {
            currentMonth--
        }
        updateMonthHeading()
        loadCategoryTotals(currentYear, currentMonth)
    }
}

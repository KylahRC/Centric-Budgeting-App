package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

@Suppress("DEPRECATION")
class AddExpenseActivity : AppCompatActivity() {

    // UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var categorySpinner: Spinner
    private lateinit var amountField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var dateField: EditText
    private lateinit var saveButton: Button

    // Data
    private var categoryIds: List<String> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // 1. Initialize UI components first
        initializeUI()

        // 2. Setup features
        setupDrawer()
        getCategories()

        // 3. Set your listeners
        dateField.setOnClickListener { showDatePicker(dateField) }
        saveButton.setOnClickListener { saveExpense() }
    }


    private fun initializeUI() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)
        categorySpinner = findViewById(R.id.spCategory)
        amountField = findViewById(R.id.etAmount)
        descriptionField = findViewById(R.id.etDescription)
        dateField = findViewById(R.id.etDate)
        saveButton = findViewById(R.id.btnSaveExpense)
    }

    private fun setupDrawer() {

        menuButton.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_add_category -> startActivity(Intent(this, AddCategoryActivity::class.java))
                //R.id.nav_add_expense -> startActivity(Intent(this, AddExpenseActivity::class.java))
                R.id.nav_budget_goals -> startActivity(Intent(this, BudgetGoalsActivity::class.java))
                //R.id.nav_view_expenses -> startActivity(Intent(this, ViewExpensesActivity::class.java))
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

    private fun getCategories() {
        categorySpinner = findViewById(R.id.spCategory)

        FirebaseInteractions.getCategories { categories ->
            val names = categories.map { it.name ?: "Unnamed" }
            val ids = categories.map { it.id ?: "" }

            categoryIds = ids

            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter
        }

    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker(dateField: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // spinner style
            { _, selectedYear, selectedMonth, selectedDay ->
                val formatted = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                dateField.setText(formatted)
            },
            year,
            month,
            day
        )

        datePicker.datePicker.calendarViewShown = false
        datePicker.datePicker.spinnersShown = true
        datePicker.show()
    }

    private fun saveExpense() {
        android.util.Log.d("DEBUG_SAVE", "Save button clicked - entering saveExpense function")
        val amount = amountField.text.toString().toDoubleOrNull()
        val description = descriptionField.text.toString().trim()
        val selectedIndex = categorySpinner.selectedItemPosition
        val categoryId = categoryIds.getOrNull(selectedIndex) ?: ""
        val dateText = dateField.text.toString().trim()

        if (amount != null && description.isNotEmpty() && dateText.isNotEmpty()) {
            FirebaseInteractions.addExpense(
                amount = amount,
                description = description,
                categoryId = categoryId,
                date = dateText
            ) { success ->
                if (success) {
                    Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()
                    amountField.text.clear()
                    descriptionField.text.clear()
                    dateField.text.clear()
                    categorySpinner.setSelection(0)
                } else {
                    Toast.makeText(this, "Failed to add expense", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }
}

package com.example.centricbudgetingapp

import android.os.Bundle
import android.content.Intent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class BudgetGoalsActivity : AppCompatActivity() {

    // Drawer
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton

    // UI
    private lateinit var categorySpinner: Spinner
    private lateinit var switchMinGoal: SwitchCompat
    private lateinit var switchMaxGoal: SwitchCompat
    private lateinit var etMinGoal: TextInputEditText
    private lateinit var etMaxGoal: TextInputEditText
    private lateinit var saveGoalsButton: Button

    // Data
    private var currentBalance: Long = 0L
    private var categories: List<Category> = emptyList()
    private var selectedCategoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        initUI()
        setupDrawer()
        loadBalance()
        loadCategories()
        setupSaveGoals()
    }

    private fun initUI() {
        categorySpinner = findViewById(R.id.spinnerBudgetCategory)
        switchMinGoal = findViewById(R.id.switchMinGoal)
        switchMaxGoal = findViewById(R.id.switchMaxGoal)
        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        saveGoalsButton = findViewById(R.id.btnSaveGoals)
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
            currentBalance = balance ?: 0L
        }
    }

    private fun loadCategories() {
        FirebaseInteractions.getCategories { cats ->
            categories = cats
            val names = cats.map { it.name ?: "Unnamed" }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = adapter

            categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                    selectedCategoryId = categories[position].id
                    loadGoalsForCategory(selectedCategoryId!!)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun loadGoalsForCategory(categoryId: String) {
        FirebaseInteractions.getBudgetGoalsForCategory(categoryId) { minGoal, maxGoal ->
            if (minGoal != null) {
                switchMinGoal.isChecked = true
                etMinGoal.setText(minGoal.toString())
            } else {
                switchMinGoal.isChecked = false
                etMinGoal.setText("")
            }

            if (maxGoal != null) {
                switchMaxGoal.isChecked = true
                etMaxGoal.setText(maxGoal.toString())
            } else {
                switchMaxGoal.isChecked = false
                etMaxGoal.setText("")
            }
        }
    }

    private fun setupSaveGoals() {
        saveGoalsButton.setOnClickListener {
            val categoryId = selectedCategoryId ?: return@setOnClickListener

            val minGoal = if (switchMinGoal.isChecked) etMinGoal.text.toString().toLongOrNull() else null
            val maxGoal = if (switchMaxGoal.isChecked) etMaxGoal.text.toString().toLongOrNull() else null

            FirebaseInteractions.setBudgetGoalsForCategory(categoryId, minGoal, maxGoal) { success ->
                val msg = if (success) "Goals saved!" else "Failed to save goals"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.example.centricbudgetingapp

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.text.InputType
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class BudgetGoalsActivity : AppCompatActivity() {

    // UI
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    private lateinit var balanceText: TextView
    private lateinit var addMoneyButton: Button
    private lateinit var seekMinGoal: SeekBar
    private lateinit var seekMaxGoal: SeekBar
    private lateinit var minGoalText: TextView
    private lateinit var maxGoalText: TextView
    private lateinit var saveGoalsButton: Button

    private var currentBalance: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_goals)

        initUI()
        setupDrawer()
        loadBalance()
        loadGoals()
        setupAddMoney()
        setupSliders()
        setupSaveGoals()
    }

    private fun initUI() {
        balanceText = findViewById(R.id.tvBalance)
        addMoneyButton = findViewById(R.id.btnAddMoney)
        seekMinGoal = findViewById(R.id.seekMinGoal)
        seekMaxGoal = findViewById(R.id.seekMaxGoal)
        minGoalText = findViewById(R.id.tvMinGoal)
        maxGoalText = findViewById(R.id.tvMaxGoal)
        saveGoalsButton = findViewById(R.id.btnSaveGoals)
    }

    private fun loadBalance() {
        FirebaseInteractions.getBalance { balance ->
            currentBalance = balance ?: 0L
            balanceText.text = "Balance: R$currentBalance"
            updateGoalTexts(seekMinGoal.progress, seekMaxGoal.progress)
        }
    }

    private fun loadGoals() {
        FirebaseInteractions.getBudgetGoals { minGoal, maxGoal ->
            if (minGoal != null && maxGoal != null && currentBalance > 0) {
                val minPercent = ((minGoal.toDouble() / currentBalance) * 100).toInt()
                val maxPercent = ((maxGoal.toDouble() / currentBalance) * 100).toInt()
                seekMinGoal.progress = minPercent
                seekMaxGoal.progress = maxPercent
                updateGoalTexts(minPercent, maxPercent)
            }
        }
    }

    private fun setupAddMoney() {
        addMoneyButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            dialog.setTitle("Add Money")
            dialog.setView(input)
            dialog.setPositiveButton("Add") { _, _ ->
                val amount = input.text.toString().toLongOrNull() ?: 0L
                FirebaseInteractions.addToBalance(amount) { newBalance ->
                    currentBalance = newBalance ?: currentBalance
                    balanceText.text = "Balance: R$currentBalance"
                    updateGoalTexts(seekMinGoal.progress, seekMaxGoal.progress)
                }
            }
            dialog.setNegativeButton("Cancel", null)
            dialog.show()
        }
    }


    private fun setupSliders() {
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Ensure Min doesn't exceed Max, and Max doesn't go below Min
                if (seekBar == seekMinGoal && progress > seekMaxGoal.progress) {
                    seekMaxGoal.progress = progress
                } else if (seekBar == seekMaxGoal && progress < seekMinGoal.progress) {
                    seekMinGoal.progress = progress
                }
                updateGoalTexts(seekMinGoal.progress, seekMaxGoal.progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        seekMinGoal.setOnSeekBarChangeListener(listener)
        seekMaxGoal.setOnSeekBarChangeListener(listener)
    }


    private fun setupSaveGoals() {
        saveGoalsButton.setOnClickListener {
            val minPercent = seekMinGoal.progress
            val maxPercent = seekMaxGoal.progress
            val minAmount = (currentBalance * minPercent / 100)
            val maxAmount = (currentBalance * maxPercent / 100)

            FirebaseInteractions.setBudgetGoals(minAmount, maxAmount) { success ->
                val msg = if (success) "Goals saved!" else "Failed to save goals"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateGoalTexts(minPercent: Int, maxPercent: Int) {
        val minAmount = (currentBalance * minPercent / 100)
        val maxAmount = (currentBalance * maxPercent / 100)

        minGoalText.text = "Min Goal: $minPercent% (R$minAmount)"
        maxGoalText.text = "Max Goal: $maxPercent% (R$maxAmount)"
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
}
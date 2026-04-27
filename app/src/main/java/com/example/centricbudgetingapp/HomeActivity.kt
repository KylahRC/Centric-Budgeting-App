package com.example.centricbudgetingapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import android.widget.TextView

class HomeActivity : AppCompatActivity() {

    val db = Firebase.firestore
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton

    var button1: Button? = null
    val userId = FirebaseAuth.getInstance().currentUser?.uid
//    val userRef = db.collection("users").document(userId!!)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Manually reference a known document ID
        val testUserId = "YkWuXMXXXyPSfMAT41LjswX2qyx1"
        val userRef = db.collection("users").document(testUserId)

        userRef.get().addOnSuccessListener { doc ->
            if (doc != null && doc.exists()) {
                val name = doc.getString("name")
                findViewById<TextView>(R.id.tvTest).text = "Welcome, $name"
            } else {
                findViewById<TextView>(R.id.tvTest).text = "No document found"
            }
        }.addOnFailureListener { e ->
            findViewById<TextView>(R.id.tvTest).text = "Error: ${e.message}"
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)


        // Open drawer when menu button clicked
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }



        // Handle menu item clicks
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



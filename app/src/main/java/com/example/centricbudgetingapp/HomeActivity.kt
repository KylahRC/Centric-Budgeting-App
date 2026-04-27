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

    //db firebase calls and vals
    val db = Firebase.firestore
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val userRef = db.collection("users").document(userId!!)

    //other vals
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var menuButton: ImageButton
    var button1: Button? = null //planned for moving add this and hat to a floating button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //this drawer thing is the left menu
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        menuButton = findViewById(R.id.btnMenu)

        //when you tap the button then the menu opens
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //the options on the menu
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_add_category -> startActivity(Intent(this, AddCategoryActivity::class.java))
                R.id.nav_add_expense -> startActivity(Intent(this, AddExpenseActivity::class.java))
                R.id.nav_budget_goals -> startActivity(Intent(this, BudgetGoalsActivity::class.java))
                R.id.nav_view_expenses -> startActivity(Intent(this, ViewExpensesActivity::class.java))
                R.id.nav_category_totals -> startActivity(Intent(this, CategoryTotalsActivity::class.java))
                R.id.nav_logout -> {
                    //needed a way to have the users log out
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            //closes the menu
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        //I wanted to combine this into one but it gave me issues but this works and im not touching it
        userRef.get().addOnSuccessListener { doc ->
            if (doc != null && doc.exists()) {
                val name = doc.getString("name")
                findViewById<TextView>(R.id.tvTest).text = "Connected to database for: $name"
//                val balance = doc.getString("balance")
//                findViewById<TextView>(R.id.tvBalance).text = "Budget, $balance"
            } else {
                findViewById<TextView>(R.id.tvTest).text = "No document found"
            }
        }.addOnFailureListener { e ->
            findViewById<TextView>(R.id.tvTest).text = "Error: ${e.message}"
        }

        userRef.get().addOnSuccessListener { doc ->
            if (doc != null && doc.exists()) {
//                val name = doc.getString("name")
//                findViewById<TextView>(R.id.tvTest).text = "Welcome, $name"
                val balance = doc.get("balance")
                findViewById<TextView>(R.id.tvBalance).text = "Balance: $balance"
            } else {
                findViewById<TextView>(R.id.tvTest).text = "No document found"
            }
        }.addOnFailureListener { e ->
            findViewById<TextView>(R.id.tvTest).text = "Error: ${e.message}"
        }
    }
}



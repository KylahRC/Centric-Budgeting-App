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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentReference
import com.example.centricbudgetingapp.UserData

object FirebaseInteractions {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

        fun getBalance(onResult: (Long?) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(null)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getLong("balance"))
                }
                .addOnFailureListener { onResult(null) }
        }

        fun getUsername(onResult: (String?) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(null)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getString("username"))
                }
                .addOnFailureListener { onResult(null) }
        }

        fun getEmail(onResult: (String?) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(null)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getString("email"))
                }
                .addOnFailureListener { onResult(null) }
        }

        fun getMinGoal(onResult: (Long?) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(null)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getLong("minGoal"))
                }
                .addOnFailureListener { onResult(null) }
        }

        fun getMaxGoal(onResult: (Long?) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(null)
            db.collection("users").document(userId).get()
                .addOnSuccessListener { doc ->
                    onResult(doc.getLong("maxGoal"))
                }
                .addOnFailureListener { onResult(null) }
        }

        fun getCategories(onResult: (List<Category>) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(emptyList())
            db.collection("users").document(userId).collection("categories").get()
                .addOnSuccessListener { snap ->
                    val categories = snap.documents.map { doc ->
                        Category(
                            id = doc.id,
                            name = doc.getString("name"),
                            description = doc.getString("description")
                        )
                    }
                    onResult(categories)
                }
                .addOnFailureListener { onResult(emptyList()) }
        }

        fun getExpenses(onResult: (List<Expense>) -> Unit) {
            val userId = auth.currentUser?.uid ?: return onResult(emptyList())
            db.collection("users").document(userId).collection("expenses").get()
                .addOnSuccessListener { snap ->
                    val expenses = snap.documents.map { doc ->
                        Expense(
                            id = doc.id,
                            amount = doc.getDouble("amount"),
                            description = doc.getString("description"),
                            categoryId = doc.getString("categoryId"),
                            photoUrl = doc.getString("photoUrl"),
                            date = doc.getLong("date"),
                            startTime = doc.getLong("startTime"),
                            endTime = doc.getLong("endTime")
                        )
                    }
                    onResult(expenses)
                }
                .addOnFailureListener { onResult(emptyList()) }
        }



    fun addToBalance(amount: Long, onResult: (Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { doc ->
            if (doc != null && doc.exists()) {
                val currentBalance = (doc.get("balance") as? Long) ?: 0L
                val newBalance = currentBalance + amount

                userRef.update("balance", newBalance)
                    .addOnSuccessListener {
                        Log.d("FirebaseInteractions", "Balance updated to $newBalance")
                        onResult(newBalance)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseInteractions", "Error updating balance", e)
                        onResult(null)
                    }
            } else {
                onResult(null)
            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseInteractions", "Error fetching balance", e)
            onResult(null)
        }
    }
}
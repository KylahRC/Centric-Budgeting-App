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

    fun fetchAllUserData(onResult: (UserData?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        val userDocRef = db.collection("users").document(userId)

        userDocRef.get().addOnSuccessListener { userDoc ->
            if (userDoc != null && userDoc.exists()) {
                Log.d("FirebaseInteractionsUserName", "Username field: ${userDoc.get("username")}")
                val userData = UserData(
                    username = userDoc.getString("username"),
                    email = userDoc.getString("email"),
                    minGoal = userDoc.getLong("minGoal"),
                    maxGoal = userDoc.getLong("maxGoal"),
                    balance = userDoc.getLong("balance")

                )

//                Log.d("FirebaseInteractionsBalance", "Balance field: ${userDoc.get("balance")}")
//                Log.d("FirebaseInteractionsEmail", "Email field: ${userDoc.get("email")}")
//                Log.d("FirebaseInteractionsMinGoal", "MinGoal field: ${userDoc.get("minGoal")}")



                // fetch categories
                userDocRef.collection("categories").get().addOnSuccessListener { catSnap ->
                    val categories = catSnap.documents.map { doc ->
                        Category(
                            id = doc.id,
                            name = doc.getString("name"),
                            description = doc.getString("description")
                        )
                    }

                    // fetch expenses
                    userDocRef.collection("expenses").get().addOnSuccessListener { expSnap ->
                        val expenses = expSnap.documents.map { doc ->
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

                        // return combined data
                        onResult(userData.copy(categories = categories, expenses = expenses))
                    }
                }
            } else {
                onResult(null)
            }


        }.addOnFailureListener { e ->
            Log.e("FirebaseInteractions", "Error fetching user data", e)
            onResult(null)
        }


    }
}
package com.example.centricbudgetingapp

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseInteractions {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // --- User Info ---
    fun getBalance(onResult: (Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc -> onResult(doc.getLong("balance")) }
            .addOnFailureListener { onResult(null) }
    }

    fun getUsername(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc -> onResult(doc.getString("username")) }
            .addOnFailureListener { onResult(null) }
    }

    fun getEmail(onResult: (String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc -> onResult(doc.getString("email")) }
            .addOnFailureListener { onResult(null) }
    }

    fun getMinGoal(onResult: (Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc -> onResult(doc.getLong("minGoal")) }
            .addOnFailureListener { onResult(null) }
    }

    fun getMaxGoal(onResult: (Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null)
        db.collection("users").document(userId).get()
            .addOnSuccessListener { doc -> onResult(doc.getLong("maxGoal")) }
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

    fun addCategory(name: String, description: String, onResult: (Boolean) -> Unit = {}) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val userRef = db.collection("users").document(userId)

        val category = mapOf(
            "name" to name,
            "description" to description
        )

        userRef.collection("categories").add(category)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error adding category", e)
                onResult(false)
            }
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
                        date = doc.getString("date") // formatted string
                    )
                }
                onResult(expenses)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun addExpense(
        amount: Double,
        description: String,
        categoryId: String,
        date: String,
        photoUrl: String? = null,
        onResult: (Boolean) -> Unit = {}
    ) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val userRef = db.collection("users").document(userId)

        val expense = mapOf(
            "amount" to amount,
            "description" to description,
            "categoryId" to categoryId,
            "date" to date,
            "photoUrl" to (photoUrl ?: "")
        )

        userRef.collection("expenses").add(expense)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error adding expense", e)
                onResult(false)
            }
    }
    fun getCategoryMap(onResult: (Map<String, String>) -> Unit) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("categories").get().addOnSuccessListener { snapshot ->
            val categoryMap = mutableMapOf<String, String>()
            for (doc in snapshot) {
                val name = doc.getString("name") ?: "Unknown"
                categoryMap[doc.id] = name // Map DocumentID -> Name
            }
            onResult(categoryMap)
        }
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

    fun createUserDoc(
        uid: String,
        name: String,
        username: String,
        email: String,
        dob: String,
        onResult: (Boolean) -> Unit = {}
    ) {
        val userRef = db.collection("users").document(uid)

        val userDoc = mapOf(
            "name" to name,
            "username" to username,
            "email" to email,
            "dob" to dob,
            "balance" to 0L,
            "minGoal" to 0L,
            "maxGoal" to 0L
        )

        userRef.set(userDoc)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error creating user doc", e)
                onResult(false)
            }
    }

    fun createStarterCategory(uid: String, onResult: (Boolean) -> Unit = {}) {
        val userRef = db.collection("users").document(uid)
        val starterCategory = mapOf(
            "name" to "General",
            "description" to "Default category"
        )

        userRef.collection("categories").add(starterCategory)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error creating starter category", e)
                onResult(false)
            }
    }

    fun createStarterExpense(uid: String, onResult: (Boolean) -> Unit = {}) {
        val userRef = db.collection("users").document(uid)
        val starterExpense = mapOf(
            "description" to "Welcome Expense",
            "amount" to 0.0,
            "categoryId" to "General",
            "photoUrl" to "",
            "date" to "0000-00-00"
        )

        userRef.collection("expenses").add(starterExpense)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error creating starter expense", e)
                onResult(false)
            }
    }

    fun getCategoryTotalsForMonth(year: Int, month: Int, onResult: (Map<String, Double>) -> Unit) {
        getExpenses { expenses ->
            val totals = expenses
                .filter { exp ->
                    // Matches your existing date filtering logic
                    val parts = exp.date?.split("-")
                    parts?.getOrNull(0)?.toIntOrNull() == year && parts?.getOrNull(1)?.toIntOrNull() == month
                }
                .groupBy { it.categoryId ?: "Unknown" }
                .mapValues { entry -> entry.value.sumOf { it.amount ?: 0.0 } }

            onResult(totals)
        }

    }

    fun setBudgetGoals(minGoalAmount: Long, maxGoalAmount: Long, onResult: (Boolean) -> Unit = {}) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val userRef = db.collection("users").document(userId)

        val updates = mapOf(
            "minGoal" to minGoalAmount,
            "maxGoal" to maxGoalAmount
        )

        userRef.update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error saving budget goals", e)
                onResult(false)
            }
    }

    fun getBudgetGoals(onResult: (Long?, Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null, null)
        val userRef = db.collection("users").document(userId)

        userRef.get()
            .addOnSuccessListener { doc ->
                val minGoal = doc.getLong("minGoal")
                val maxGoal = doc.getLong("maxGoal")
                onResult(minGoal, maxGoal)
            }
            .addOnFailureListener { onResult(null, null) }
    }

    // In your FirebaseInteractions.kt file, inside the object:
    fun updateExpenseAmount(expenseId: String, newAmount: Double, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(false)

        // Correct Path: users/{userId}/expenses/{expenseId}
        db.collection("users").document(userId).collection("expenses").document(expenseId)
            .update("amount", newAmount)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { e ->
                Log.e("FirebaseInteractions", "Error updating expense", e)
                callback(false)
            }
    }

    fun getCombinedCategories(onResult: (List<String>) -> Unit) {
        val standardCategories = listOf("Food", "Transport", "Rent", "Entertainment", "Utilities")

        // This assumes your existing function is called getCategories
        getCategories { customCategories ->
            // Extract the names from your custom category objects
            val customNames = customCategories.map { it.name ?: "Unnamed" }

            // Merge the lists and remove duplicates
            val allCategories = (standardCategories + customNames).distinct()
            onResult(allCategories)
        }
    }

    fun setBudgetGoalsForCategory(categoryId: String, minGoal: Long?, maxGoal: Long?, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(false)
        val userRef = db.collection("users").document(userId)
            .collection("categories").document(categoryId)

        val updates = mutableMapOf<String, Any>()
        minGoal?.let { updates["minGoal"] = it }
        maxGoal?.let { updates["maxGoal"] = it }

        userRef.update(updates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getBudgetGoalsForCategory(categoryId: String, onResult: (Long?, Long?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return onResult(null, null)
        val userRef = db.collection("users").document(userId)
            .collection("categories").document(categoryId)

        userRef.get()
            .addOnSuccessListener { doc ->
                val minGoal = doc.getLong("minGoal")
                val maxGoal = doc.getLong("maxGoal")
                onResult(minGoal, maxGoal)
            }
            .addOnFailureListener { onResult(null, null) }
    }


}


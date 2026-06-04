package com.example.centricbudgetingapp

data class UserData(
    val username: String? = null,
    val email: String? = null,
    val minGoal: Long? = null,
    val maxGoal: Long? = null,
    val balance: Long? = null,
    val categories: List<Category> = emptyList(),
    val expenses: List<Expense> = emptyList()
)

data class Category(
    val id: String,
    val name: String?,
    val description: String?
)

data class Expense(
    val id: String,
    val amount: Double? = null,
    val description: String? = null,
    val categoryId: String? = null,
    val photoUrl: String? = null,
    val date: String? = null   // formatted YYYY-MM-DD string
)



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
    val id: String = "",
    val name: String? = null,
    val description: String? = null
)

data class Expense(
    val id: String = "",
    val amount: Double? = null,
    val description: String? = null,
    val categoryId: String? = null,
    val photoUrl: String? = null,
    val date: Long? = null,
    val startTime: Long? = null,
    val endTime: Long? = null
)


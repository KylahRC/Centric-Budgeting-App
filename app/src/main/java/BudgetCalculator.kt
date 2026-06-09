package com.example.centricbudgetingapp

object BudgetCalculator {
    // Splits total income into (Needs, Wants)
    // Needs default to 60% (0.6), Wants get the rest (0.4)
    fun calculateSplit(total: Double, needsPercent: Double = 0.6): Pair<Double, Double> {
        val needs = total * needsPercent
        val wants = total * (1.0 - needsPercent)
        return Pair(needs, wants)
    }
}
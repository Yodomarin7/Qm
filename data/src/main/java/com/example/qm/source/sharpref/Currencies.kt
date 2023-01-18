package com.example.qm.source.sharpref

import android.content.Context
import com.example.qm.model.SortCurrency

private const val SHARED_PREFS_NAME = "Currencies"

private const val SORTING = "sorting"

class Currencies(context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun getSorting(): SortCurrency {
        var result = SortCurrency.ByName

        val sorting = sharedPreferences.getString(SORTING, null)
        if(sorting == "BY_SUM") result = SortCurrency.BySum

        return result
    }

    fun setSorting(sort: SortCurrency) {
        var result = "BY_NAME"
        if(sort == SortCurrency.BySum) result = "BY_SUM"

        sharedPreferences.edit().putString(SORTING, result).apply()
    }
}
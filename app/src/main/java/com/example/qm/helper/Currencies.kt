package com.example.qm.helper

import android.content.Context
import androidx.compose.ui.text.toUpperCase
import com.example.qm.R
import java.util.*

class Currencies(private val context: Context) {
    private val items = mutableMapOf<String, Int>()

    init {
        items["AED"] = R.string.AED
        items["AMD"] = R.string.AMD
        items["AZN"] = R.string.AZN
        items["BYN"] = R.string.BYN
        items["CAD"] = R.string.CAD
        items["CHF"] = R.string.CHF
        items["CNY"] = R.string.CNY
        items["EUR"] = R.string.EUR
        items["GBP"] = R.string.GBP
        items["GEL"] = R.string.GEL
        items["HKD"] = R.string.HKD
        items["IDR"] = R.string.IDR
        items["INR"] = R.string.INR
        items["JPY"] = R.string.JPY
        items["KGS"] = R.string.KGS
        items["KZT"] = R.string.KZT
        items["RUB"] = R.string.RUB
        items["TJS"] = R.string.TJS
        items["TRY"] = R.string.TRY
        items["UAH"] = R.string.UAH
        items["USD"] = R.string.USD
        items["UZS"] = R.string.UZS
    }

    fun getItem(code: String): Int? {
        return items[code]
    }

    fun getItems(filter: String): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        items.asSequence().filter { map->
            val words = context.getString(map.value).split(" ")
            var r = false

            for (word in words) {
                if(word.startsWith(filter, ignoreCase = true)) {
                    r = true
                    break
                }
            }

            r
        }.take(5).forEach { result[it.key] = it.value }

        if(result.isEmpty()) {
            items.asSequence().filter { map->
                map.key.contains(filter, ignoreCase = true)
            }.take(5).forEach { result[it.key] = it.value }
        }

        return result
    }
}
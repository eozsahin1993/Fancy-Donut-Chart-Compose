package com.example.donutchart.misc

import java.text.DecimalFormat

private const val MONEY_FORMAT = "###,###,##0.00"
private const val MONEY_FORMAT_NO_CENTS = "###,###,###"

fun Float.toMoneyFormat(
    removeTrailingZeroes: Boolean = false,
): String {
    val format = if (removeTrailingZeroes && (this % 1 == 0.0f)) DecimalFormat(MONEY_FORMAT_NO_CENTS)
        else DecimalFormat(MONEY_FORMAT)

    return format.format(this)
}
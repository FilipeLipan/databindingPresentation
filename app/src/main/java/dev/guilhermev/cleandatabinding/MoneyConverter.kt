package dev.guilhermev.cleandatabinding

import android.content.Context
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.InverseMethod
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object MoneyConverter {

    @JvmStatic
    fun doubleToText(view: TextView, value: Double): String {
        val locale = getDeviceLanguageLocale(view.context)
        return convertDoubleToMoneyText(value, locale)
    }

    @JvmStatic
    @InverseMethod("doubleToText")
    fun textToDouble(view: TextView, value: String): Double {
        if (view is EditText)
            view.setSelection(value.length)

        return convertMoneyTextToDouble(value)
    }

    // E.g.: "1,00" -> 1.00; "3 -> 0.03"
    @JvmStatic
    fun convertMoneyTextToDouble(text: String, scale: Int = 2): Double {
        if (scale <= 0)
            throw IllegalArgumentException("places must be greater than 0")

        val divideFactor = 100
        val onlyDigits = text.filter { it.isDigit() }

        return BigDecimal(onlyDigits)
            .setScale(scale, BigDecimal.ROUND_HALF_UP)
            .divide(divideFactor.toBigDecimal(), BigDecimal.ROUND_HALF_UP)
            .toDouble()
    }

    // Eg.: "3" -> "3.00"
    @JvmStatic
    fun convertDoubleToMoneyText(number: Number, locale: Locale, scale: Int = 2): String {
        if (scale <= 0)
            throw IllegalArgumentException("places must be greater than 0")

        // https://developer.android.com/reference/java/text/DecimalFormat.html
        val pattern = StringBuilder().apply { append("#,##0.") }

        for (i in 0 until scale) {
            pattern.append("0")
        }

        val df = DecimalFormat(pattern.toString(), DecimalFormatSymbols(locale)).apply {
            roundingMode = RoundingMode.DOWN }

        return df.format(number)
    }

    @JvmStatic
    fun getDeviceLanguageLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) context.resources.configuration.locales[0]
        else context.resources.configuration.locale
    }
}
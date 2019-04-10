package dev.guilhermev.cleandatabinding

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val locale by lazy {
        getCellphoneSettingsLocale(this@MainActivity)
    }

    private val dollarTextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val text = s.toString().filter { it.isDigit() }

                if (text.isNotBlank()) {
                    val doubleValue = currencyStringToDouble(text)
                    viewModel.setDollarValue(doubleValue)
                }
            }
        }
    }

    private val otherCurrencyTextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val text = s.toString().filter { it.isDigit() }

                if (text.isNotBlank()) {
                    val otherCurrencyValue = currencyStringToDouble(text)
                    viewModel.setOtherCurrencyValue(otherCurrencyValue)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null)
            return

        dollarInput.addTextChangedListener(dollarTextWatcher)

        viewModel.getDollarValue().observe(this, androidx.lifecycle.Observer { value ->
            dollarInput.removeTextChangedListener(dollarTextWatcher)

            val currencyString = doubleToCurrency(value, locale)
            dollarInput.setText(currencyString)
            dollarInput.setSelection(dollarInput.text.length)

            dollarInput.addTextChangedListener(dollarTextWatcher)
        })


        otherCurrencyInput.addTextChangedListener(otherCurrencyTextWatcher)

        viewModel.getOtherCurrencyValue().observe(this, androidx.lifecycle.Observer { value ->
            otherCurrencyInput.removeTextChangedListener(otherCurrencyTextWatcher)

            val currencyString = doubleToCurrency(value, locale)
            otherCurrencyInput.setText(currencyString)
            otherCurrencyInput.setSelection(otherCurrencyInput.text.length)

            otherCurrencyInput.addTextChangedListener(otherCurrencyTextWatcher)
        })

        viewModel.getRate().observe(this, androidx.lifecycle.Observer { value ->
            rate.text = "Rate: $value"
        })
    }


    fun currencyStringToDouble(text: String, scale: Int = 2): Double {
        if (scale <= 0) {
            throw IllegalArgumentException("scale must be greater than 0")
        }

        val divideFactor = 100
        val onlyDigits = text.filter { it.isDigit() }

        return BigDecimal(onlyDigits)
            .setScale(scale, BigDecimal.ROUND_HALF_UP)
            .divide(divideFactor.toBigDecimal(), BigDecimal.ROUND_HALF_UP)
            .toDouble()
    }

    fun doubleToCurrency(number: Number, locale: Locale, scale: Int = 2): String {
        if (scale <= 0) {
            throw IllegalArgumentException("scale must be greater than 0")
        }

        // https://developer.android.com/reference/java/text/DecimalFormat.html
        val pattern = StringBuilder().apply { append("#,##0.") }

        for (i in 0 until scale) {
            pattern.append("0")
        }

        val df = DecimalFormat(pattern.toString(), DecimalFormatSymbols(locale)).apply {
            roundingMode = RoundingMode.DOWN
        }

        return df.format(number)
    }


    private fun getCellphoneSettingsLocale(context: Context) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales[0]
        else
            context.resources.configuration.locale
}

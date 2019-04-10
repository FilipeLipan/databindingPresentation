package dev.guilhermev.cleandatabinding

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val dollarValue = mutableLiveData(0.0)
    private val otherCurrencyValue = mutableLiveData(0.0)

    private val rate = mutableLiveData(3.0)


    fun getDollarValue() = dollarValue

    fun setDollarValue(value: Double) {
        if (dollarValue.value == value) return

        dollarValue.value = value
        otherCurrencyValue.value = value * rate.value!!
    }


    fun getOtherCurrencyValue() = otherCurrencyValue

    fun setOtherCurrencyValue(value: Double) {
        if (otherCurrencyValue.value == value) return

        otherCurrencyValue.value = value
        dollarValue.value = value / rate.value!!
    }

    fun getRate() = rate
}
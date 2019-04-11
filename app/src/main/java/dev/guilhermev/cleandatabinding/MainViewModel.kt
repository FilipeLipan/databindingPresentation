package dev.guilhermev.cleandatabinding

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val dollarValue = mutableLiveData(0.0)
    val otherCurrencyValue = mutableLiveData(0.0)
    private val rate = mutableLiveData(3.0)

    fun recalculateOtherCurrency() {
        otherCurrencyValue.value = rate.value!! * dollarValue.value!!
    }

    fun recalculateDollar() {
        dollarValue.value = otherCurrencyValue.value!! / rate.value!!
    }

    fun getRate() = rate
}
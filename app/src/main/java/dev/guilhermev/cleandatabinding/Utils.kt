package dev.guilhermev.cleandatabinding

import androidx.lifecycle.MutableLiveData

fun <T : Any> mutableLiveData(initialValue: T? = null): MutableLiveData<T> {
    return MutableLiveData<T>().apply { value = initialValue }
}